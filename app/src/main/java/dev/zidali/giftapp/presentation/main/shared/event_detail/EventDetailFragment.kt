package dev.zidali.giftapp.presentation.main.shared.event_detail

import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.*
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dev.zidali.giftapp.R
import dev.zidali.giftapp.business.domain.models.ContactEvent
import dev.zidali.giftapp.business.domain.util.*
import dev.zidali.giftapp.databinding.FragmentAllEventsBinding
import dev.zidali.giftapp.databinding.FragmentEventDetailBinding
import dev.zidali.giftapp.presentation.main.BaseMainFragment
import dev.zidali.giftapp.presentation.main.contacts.contact_detail.gift.GiftEvents
import dev.zidali.giftapp.presentation.main.fab.create_event.ReminderFragment
import dev.zidali.giftapp.presentation.notification.AlarmScheduler
import dev.zidali.giftapp.presentation.update.GlobalEvents
import dev.zidali.giftapp.util.TopSpacingItemDecoration
import dev.zidali.giftapp.util.processQueue
import java.text.SimpleDateFormat
import java.util.*

class EventDetailFragment : BaseMainFragment() {

    private val viewModel: EventDetailViewModel by viewModels()
    private var _binding: FragmentEventDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        subscribeObservers()

        binding.contactName.inputType = InputType.TYPE_NULL
        binding.inputEvent.inputType = InputType.TYPE_NULL

        binding.editButton.setOnClickListener {
            val bundle = bundleOf()
            bundle.putString("CONTACT_NAME", viewModel.state.value?.contact_event?.contact_name)
            bundle.putString("CONTACT_EVENT", viewModel.state.value?.contact_event?.contact_event)
            findNavController().navigate(R.id.action_eventDetailFragment_to_editEventFragment, bundle)
        }
    }

    override fun onResume() {
        super.onResume()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun subscribeObservers() {

        globalManager.state.observe(viewLifecycleOwner) { state ->

            if (state.needToUpdate) {
                viewModel.onTriggerEvent(EventDetailEvents.Refresh)
                globalManager.onTriggerEvent(GlobalEvents.SetNeedToUpdate(false))
            }
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->

            state.contact_event?.let { setEventProperties(it) }

            processQueue(
                context = context,
                queue = state.queue,
                stateMessageCallback = object : StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.onTriggerEvent(EventDetailEvents.OnRemoveHeadFromQueue)
                    }
                }
            )
        }
    }

    private fun setEventProperties(contactEvent: ContactEvent) {

        val calendar = Calendar.getInstance()

        calendar.set(Calendar.YEAR, contactEvent.year)
        calendar.set(Calendar.MONTH, contactEvent.month)
        calendar.set(Calendar.DAY_OF_MONTH, contactEvent.day)

        val userSelection = SimpleDateFormat("MM-dd-yyyy", Locale.ENGLISH).format(calendar.time)

        binding.contactName.setText(contactEvent.contact_name)
        binding.inputEvent.setText(contactEvent.contact_event)
        binding.datePicker.setText(userSelection)
        if(contactEvent.contact_event_reminder != "") {
            binding.reminderPicker.setText(contactEvent.contact_event_reminder)
        } else {
            binding.reminderPicker.setText(R.string.none)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}