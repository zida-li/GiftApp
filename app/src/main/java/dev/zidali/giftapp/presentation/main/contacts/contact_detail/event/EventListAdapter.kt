package dev.zidali.giftapp.presentation.main.contacts.contact_detail.event

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import dev.zidali.giftapp.business.domain.models.ContactEvent
import dev.zidali.giftapp.databinding.GiftListItemBinding

class EventListAdapter(private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ContactEvent>() {

        override fun areItemsTheSame(oldItem: ContactEvent, newItem: ContactEvent): Boolean {
            return oldItem.contact_event == newItem.contact_event
        }

        override fun areContentsTheSame(oldItem: ContactEvent, newItem: ContactEvent): Boolean {
            return oldItem == newItem
        }

    }

    private val differ = AsyncListDiffer(
        GiftRecyclerChangeCallback(this),
        AsyncDifferConfig.Builder(DIFF_CALLBACK).build()
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return EventViewHolder(
            GiftListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            interaction
        )
    }

    internal inner class GiftRecyclerChangeCallback(
        private val adapter: EventListAdapter
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
            is EventViewHolder -> {
                holder.bind(differ.currentList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: MutableList<ContactEvent>) {
        differ.submitList(list)
    }

    class EventViewHolder
    constructor(
        private val binding: GiftListItemBinding,
        private val interaction: Interaction?,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ContactEvent) = with(itemView) {
            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)
            }

            binding.giftName.text = item.contact_event

        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: ContactEvent)
    }
}