package dev.zidali.giftapp.presentation.main.contacts.contact_detail.gift

import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.*
import dev.zidali.giftapp.R
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

            checkCheckedOnInit(item)

            itemView.setOnClickListener {
                if(!item.isMultiSelectionModeEnabled) {
                    checkItemClicked(item)
                    interaction?.onIsCheckedClicked(item, adapterPosition)
                }
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
                        binding.giftCardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.primary_color))
                        setNameColor(Color.WHITE)
                        setCheckBox(R.drawable.ic_baseline_check_box_outline_blank_24_white)
                        if(mGift.isChecked) {
                            setCheckBox(R.drawable.ic_baseline_check_box_24_white)
                        }
                    }
                    else {
                        if(item.isChecked) {
                            binding.giftCardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.inactive_background))
                            setNameColor(ContextCompat.getColor(context, R.color.light_gray))
                            setCheckBox(R.drawable.ic_baseline_check_box_24)
                        } else {
                            binding.giftCardView.setCardBackgroundColor(Color.WHITE)
                            setNameColor(Color.BLACK)
                            setCheckBox(R.drawable.ic_baseline_check_box_outline_blank_24)
                        }
                    }
                } else {
                    if(item.isChecked) {
                        binding.giftCardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.inactive_background))
                        setNameColor(ContextCompat.getColor(context, R.color.light_gray))
                        setCheckBox(R.drawable.ic_baseline_check_box_24)
                    } else {
                        binding.giftCardView.setCardBackgroundColor(Color.WHITE)
                        setNameColor(Color.BLACK)
                        setCheckBox(R.drawable.ic_baseline_check_box_outline_blank_24)
                    }
                }

            }

        }

        private fun setNameColor(color: Int) {
            binding.giftName.setTextColor(color)
        }

        private fun setCheckBox(resId: Int) {
            binding.checkbox.setImageResource(resId)
        }

        private fun checkCheckedOnInit(
            item: Gift,
        ) {
            if (item.isChecked) {
                binding.giftName.apply {
                    text = item.contact_gift
                    paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    binding.checkbox.setImageResource(R.drawable.ic_baseline_check_box_24)
                    binding.giftCardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.inactive_background))
                }
            } else if (!item.isChecked) {
                binding.giftName.apply {
                    text = item.contact_gift
                    paintFlags = paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    binding.checkbox.setImageResource(R.drawable.ic_baseline_check_box_outline_blank_24)
                    binding.giftCardView.setCardBackgroundColor(Color.WHITE)
                }
            }
        }

        private fun checkItemClicked(item: Gift) {

            binding.giftName.apply {
                if (!paint.isStrikeThruText) {
                    text = item.contact_gift
                    paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    binding.checkbox.setImageResource(R.drawable.ic_baseline_check_box_24)
                    binding.giftCardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.inactive_background))
                } else {
                    binding.giftName.apply {
                        text = item.contact_gift
                        paintFlags = paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                        binding.checkbox.setImageResource(R.drawable.ic_baseline_check_box_outline_blank_24)
                        binding.giftCardView.setCardBackgroundColor(Color.WHITE)
                    }
                }
            }
        }
    }

    interface Interaction {

        fun onItemSelected(position: Int, item: Gift)

        fun activateMultiSelectionMode()

        fun isMultiSelectionModeEnabled(): Boolean

        fun onIsCheckedClicked(item: Gift, position: Int)

    }
}