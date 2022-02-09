package dev.zidali.giftapp.presentation.main.contacts.contact_detail.gift

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.*
import dev.zidali.giftapp.R
import dev.zidali.giftapp.business.domain.models.Contact
import dev.zidali.giftapp.business.domain.models.Gift
import dev.zidali.giftapp.databinding.GiftListItemBinding

class GiftListAdapter(
    private val interaction: Interaction? = null,
    private val lifecycleOwner: LifecycleOwner,
    private val selectedGifts: LiveData<ArrayList<Gift>>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Gift>() {

        override fun areItemsTheSame(oldItem: Gift, newItem: Gift): Boolean {
            return oldItem.contact_gift == newItem.contact_gift
        }

        override fun areContentsTheSame(oldItem: Gift, newItem: Gift): Boolean {
            return oldItem == newItem
        }

    }

    private val differ = AsyncListDiffer(
        GiftRecyclerChangeCallback(this),
        AsyncDifferConfig.Builder(DIFF_CALLBACK).build()
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return GiftViewHolder(
            GiftListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            interaction,
            lifecycleOwner,
            selectedGifts,
        )
    }

    internal inner class GiftRecyclerChangeCallback(
        private val adapter: GiftListAdapter
    ) : ListUpdateCallback {

        override fun onChanged(position: Int, count: Int, payload: Any?) {
            adapter.notifyItemRangeChanged(position, count, payload)
        }

        override fun onInserted(position: Int, count: Int) {
            adapter.notifyItemRangeChanged(position, count)
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            adapter.notifyDataSetChanged()
        }

        override fun onRemoved(position: Int, count: Int) {
            adapter.notifyDataSetChanged()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is GiftViewHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: MutableList<Gift>) {
        differ.submitList(list)
    }

    class GiftViewHolder
    constructor(
        private val binding: GiftListItemBinding,
        private val interaction: Interaction?,
        private val lifecycleOwner: LifecycleOwner,
        private val selectedGifts: LiveData<ArrayList<Gift>>,
    ) : RecyclerView.ViewHolder(binding.root) {

        lateinit var mGift: Gift

        fun bind(item: Gift) = with(itemView) {
            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)
            }
            setOnLongClickListener {
                interaction?.activateMultiSelectionMode()
                interaction?.onItemSelected(adapterPosition, mGift)
                true
            }
            mGift = item

            binding.giftName.text = item.contact_gift

            selectedGifts.observe(lifecycleOwner) {gift->

                if(gift != null) {
                    if (gift.contains(mGift)) {
                        binding.giftCardView.setBackgroundColor(ContextCompat.getColor(context, R.color.primary_color))
                    }
                    else {
                        binding.giftCardView.setBackgroundColor(Color.WHITE)
                    }
                } else {
                    binding.giftCardView.setBackgroundColor(Color.WHITE)
                }

            }

        }
    }

    interface Interaction {

        fun onItemSelected(position: Int, item: Gift)

        fun activateMultiSelectionMode()

        fun isMultiSelectionModeEnabled(): Boolean

    }
}