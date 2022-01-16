package dev.zidali.giftapp.presentation.main.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import dev.zidali.giftapp.business.domain.models.ContactEvent
import dev.zidali.giftapp.business.domain.util.Converters
import dev.zidali.giftapp.databinding.AllEventViewItemBinding
import dev.zidali.giftapp.databinding.ContactViewEventItemBinding
import dev.zidali.giftapp.databinding.GiftListItemBinding

class AllEventListAdapter(private val interaction: Interaction? = null) :
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
            AllEventViewItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            interaction
        )
    }

    internal inner class GiftRecyclerChangeCallback(
        private val adapter: AllEventListAdapter
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
        private val binding: AllEventViewItemBinding,
        private val interaction: Interaction?,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ContactEvent) = with(itemView) {
            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)
            }

            binding.contactName.text = item.contact_name
            binding.event.text = item.contact_event
            binding.year.text = item.year.toString()
            binding.month.text = Converters.convertIntMonthToTextMonth(item.month)
            binding.date.text = item.day.toString()

        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: ContactEvent)
    }
}