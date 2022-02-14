package dev.zidali.giftapp.presentation.main.contacts.contact_detail.event

import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.*
import dev.zidali.giftapp.R
import dev.zidali.giftapp.business.domain.models.ContactEvent
import dev.zidali.giftapp.business.domain.util.Converters
import dev.zidali.giftapp.databinding.ContactViewEventItemBinding
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.ArrayList

class EventListAdapter(
    private val interaction: Interaction? = null,
    private val lifecycleOwner: LifecycleOwner,
    private val selectedContactEvents: LiveData<ArrayList<ContactEvent>>
) :
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
            ContactViewEventItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            interaction,
            lifecycleOwner,
            selectedContactEvents
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
        private val binding: ContactViewEventItemBinding,
        private val interaction: Interaction?,
        private val lifecycleOwner: LifecycleOwner,
        private val selectedContactEvents: LiveData<ArrayList<ContactEvent>>,
    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var mContactEvent: ContactEvent

        fun bind(item: ContactEvent) = with(itemView) {

            val today = Calendar.getInstance()

            val alarmDate = Calendar.getInstance(Locale.getDefault())
            alarmDate.set(Calendar.MONTH, item.month)
            alarmDate.set(Calendar.DAY_OF_MONTH, item.day)
            alarmDate.set(Calendar.YEAR, item.year)

            if(today > alarmDate) {
//                Log.d(Constants.TAG, "today > alarmDate")
                binding.event.apply {
                    text = item.contact_event
                    typeface.isBold
                    paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                }
                binding.year.apply {
                    text = item.year.toString()
                    typeface.isBold
                    paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                }
                binding.month.apply {
                    text = Converters.convertIntMonthToTextMonth(item.month)
                    typeface.isBold
                    paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                }
                binding.date.apply {
                    val date = item.day.toString()
                    val dateWithComma = StringBuilder()
                    dateWithComma.append("$date,")
                    text = dateWithComma
                    paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                }
                binding.notificationIcon.isInvisible = true
            } else {
                binding.event.text = item.contact_event
                binding.year.text = item.year.toString()
                binding.month.text = Converters.convertIntMonthToTextMonth(item.month)
                val date = item.day.toString()
                val dateWithComma = StringBuilder()
                dateWithComma.append("$date,")
                binding.date.text = dateWithComma
            }


            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)
            }
            setOnLongClickListener {
                interaction?.activateMultiSelectionMode()
                interaction?.onItemSelected(adapterPosition, mContactEvent)
                true
            }
            mContactEvent = item

            binding.notificationIcon.setOnClickListener {

                if(item.contact_event_reminder.contains("day", true) ||
                    item.contact_event_reminder.contains("week", true) ||
                    item.contact_event_reminder.contains("month",true)) {

                    interaction?.turnOffNotifications(item)
                    binding.notificationIcon.setImageResource(R.drawable.ic_baseline_notifications_24_inactive)

                } else {

                    interaction?.turnOnNotifications(item, adapterPosition)

                }
            }

            if(item.contact_event_reminder.contains("day", true) ||
                item.contact_event_reminder.contains("week", true) ||
                item.contact_event_reminder.contains("month",true)) {
                binding.notificationIcon.setImageResource(R.drawable.ic_baseline_notifications_active_24)
            } else {
                binding.notificationIcon.setImageResource(R.drawable.ic_baseline_notifications_24_inactive)
            }

            selectedContactEvents.observe(lifecycleOwner) {contactEvent->

                if(contactEvent != null) {
                    if (contactEvent.contains(mContactEvent)) {
                        binding.eventCardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.primary_color))
                        setTextColor(Color.WHITE)
                    }
                    else {
                        if(today > alarmDate) {
                            binding.eventCardView.setCardBackgroundColor(Color.GRAY)
                            setTextColor(Color.BLACK)
                        } else {
                            binding.eventCardView.setCardBackgroundColor(Color.WHITE)
                            setTextColor(Color.BLACK)
                        }
                    }
                } else {
                    if(today > alarmDate) {
                        binding.eventCardView.setCardBackgroundColor(Color.GRAY)
                        setTextColor(Color.BLACK)
                    } else {
                        binding.eventCardView.setCardBackgroundColor(Color.WHITE)
                        setTextColor(Color.BLACK)
                    }
                }

            }

        }

        private fun setTextColor(color: Int) {
            binding.event.setTextColor(color)
            binding.year.setTextColor(color)
            binding.month.setTextColor(color)
            binding.date.setTextColor(color)
        }
    }

    interface Interaction {

        fun onItemSelected(position: Int, item: ContactEvent)

        fun activateMultiSelectionMode()

        fun isMultiSelectionModeEnabled(): Boolean

        fun turnOffNotifications(item: ContactEvent)

        fun turnOnNotifications(item: ContactEvent, position: Int)

    }
}