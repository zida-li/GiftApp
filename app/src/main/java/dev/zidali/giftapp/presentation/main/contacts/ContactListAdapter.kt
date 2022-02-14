package dev.zidali.giftapp.presentation.main.contacts

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.*
import dev.zidali.giftapp.R
import dev.zidali.giftapp.business.domain.models.Contact
import dev.zidali.giftapp.databinding.ContactListItemBinding

class ContactListAdapter(
    private val interaction: Interaction? = null,
    private val lifecycleOwner: LifecycleOwner,
    private val selectedContacts: LiveData<ArrayList<Contact>>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Contact>() {

        override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem.contact_name == newItem.contact_name
        }

        override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem == newItem
        }

    }

    private val differ = AsyncListDiffer(
        ContactRecyclerChangeCallback(this),
        AsyncDifferConfig.Builder(DIFF_CALLBACK).build()
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return ContactViewHolder(
            ContactListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            interaction,
            lifecycleOwner,
            selectedContacts,
        )
    }

    internal inner class ContactRecyclerChangeCallback(
        private val adapter: ContactListAdapter
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
            is ContactViewHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: MutableList<Contact>) {
        differ.submitList(list)
    }

    class ContactViewHolder
    constructor(
        private val binding: ContactListItemBinding,
        private val interaction: Interaction?,
        private val lifecycleOwner: LifecycleOwner,
        private val selectedContacts: LiveData<ArrayList<Contact>>,
    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var mContact: Contact

        fun bind(item: Contact) = with(itemView) {

            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)
            }
            setOnLongClickListener {
                interaction?.activateMultiSelectionMode()
                interaction?.onItemSelected(adapterPosition, mContact)
                true
            }
            mContact = item

            binding.contactName.text = item.contact_name

            selectedContacts.observe(lifecycleOwner) { contact->

                if(contact != null) {
                    if (contact.contains(mContact)) {
                        binding.contactCardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.primary_color))
                        setTextColor(Color.WHITE)
                    }
                    else {
                        binding.contactCardView.setCardBackgroundColor(Color.WHITE)
                        setTextColor(Color.BLACK)
                    }
                } else {
                    binding.contactCardView.setCardBackgroundColor(Color.WHITE)
                    setTextColor(Color.BLACK)
                }
            }

        }

        private fun setTextColor(color: Int) {
            binding.contactName.setTextColor(color)
        }
    }

    interface Interaction {

        fun onItemSelected(position: Int, item: Contact)

        fun activateMultiSelectionMode()

        fun isMultiSelectionModeEnabled(): Boolean

    }
}