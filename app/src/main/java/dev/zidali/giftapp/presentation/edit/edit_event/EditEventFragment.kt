package dev.zidali.giftapp.presentation.edit.edit_event

import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dev.zidali.giftapp.R
import dev.zidali.giftapp.business.domain.models.ContactEvent
import dev.zidali.giftapp.business.domain.util.*
import dev.zidali.giftapp.databinding.FragmentAllEventsBinding
import dev.zidali.giftapp.databinding.FragmentEditEventBinding
import dev.zidali.giftapp.databinding.FragmentEventDetailBinding
import dev.zidali.giftapp.presentation.edit.BaseEditFragment
import dev.zidali.giftapp.presentation.edit.EditEventActivity
import dev.zidali.giftapp.presentation.main.BaseMainFragment
import dev.zidali.giftapp.presentation.main.all_events.AllEventEvents
import dev.zidali.giftapp.presentation.main.all_events.AllEventToolbarState
import dev.zidali.giftapp.presentation.main.fab.create_event.CreateEventEvents
import dev.zidali.giftapp.presentation.main.fab.create_event.DatePickerFragment
import dev.zidali.giftapp.presentation.main.fab.create_event.ReminderFragment
import dev.zidali.giftapp.presentation.notification.AlarmScheduler
import dev.zidali.giftapp.presentation.update.GlobalEvents
import dev.zidali.giftapp.util.TopSpacingItemDecoration
import dev.zidali.giftapp.util.processQueue
import kotlinx.coroutines.selects.select
import java.text.SimpleDateFormat
import java.util.*

class EditEventFragment : BaseEditFragment() {

    private val viewModel: EditEventViewModel by viewModels()
    private var _binding: FragmentEditEventBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditEventBinding.inflate(layoutInflater)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        subscribeObservers()
        setOnClickListeners()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun subscribeObservers() {

        viewModel.state.observe(viewLifecycleOwner) { state ->

            if(!state.initialLoadComplete) {
                state.contact_event?.let { setEventProperties(it) }
            }

            (activity as EditEventActivity).supportActionBar?.title = state.contact_event?.contact_name

            if(state.editEventSuccessful) {
                globalManager.onTriggerEvent(GlobalEvents.SetNeedToUpdate(true))
                if(state.update_contact_event?.contact_event_reminder == "None") {
                    AlarmScheduler.cancelScheduledAlarmForReminder(requireContext(), state.update_contact_event, "day")
                    AlarmScheduler.cancelScheduledAlarmForReminder(requireContext(), state.update_contact_event, "week")
                    AlarmScheduler.cancelScheduledAlarmForReminder(requireContext(), state.update_contact_event, "month")
                } else {
                    AlarmScheduler.scheduleInitialAlarmsForReminder(requireContext(),
                        state.update_contact_event!!)
                }
                findNavController().popBackStack()
            }

            processQueue(
                context = context,
                queue = state.queue,
                stateMessageCallback = object : StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.onTriggerEvent(EditEventEvents.OnRemoveHeadFromQueue)
                    }
                }
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.edit_event_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId) {
            R.id.action_save -> {
                cacheState()
            }
            R.id.action_delete -> {

            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun cacheState() {
        viewModel.onTriggerEvent(EditEventEvents.OnUpdateEvent(binding.inputEvent.text.toString()))
        viewModel.onTriggerEvent(EditEventEvents.UpdateContactEvent)
    }

    private fun setEventProperties(contactEvent: ContactEvent) {

        val calendar = Calendar.getInstance()

        calendar.set(Calendar.YEAR, contactEvent.year)
        calendar.set(Calendar.MONTH, contactEvent.month)
        calendar.set(Calendar.DAY_OF_MONTH, contactEvent.day)

        val userSelection = SimpleDateFormat("MM-dd-yyyy", Locale.ENGLISH).format(calendar.time)

        binding.inputEvent.setText(contactEvent.contact_event)
        binding.datePicker.setText(userSelection)
        if(contactEvent.contact_event_reminder != "") {
            binding.reminderPicker.setText(contactEvent.contact_event_reminder)
        } else {
            binding.reminderPicker.setText(R.string.none)
        }
    }

    private fun setOnClickListeners() {
        binding.datePicker.setOnClickListener {
            val datePickerFragment = DatePickerFragment()
            val supportFragmentManager = requireActivity().supportFragmentManager

            supportFragmentManager.setFragmentResultListener(
                "DATE_PICKER_RESULT",
                viewLifecycleOwner
            ) { resultKey, bundle->
                if(resultKey == "DATE_PICKER_RESULT") {
                    val userSelection = bundle.getString("USER_SELECTION")
                    val dataBaseFormat = bundle.getString("SELECTED_YMD")
                    binding.datePicker.setText(userSelection)

                    val selectedYear = bundle.getInt("SELECTED_YEAR")
                    val selectedMonth = bundle.getInt("SELECTED_MONTH")
                    val selectedDate = bundle.getInt("SELECTED_DATE")

                    viewModel.onTriggerEvent(EditEventEvents.OnUpdateYmdFormat(dataBaseFormat!!))
                    viewModel.onTriggerEvent(EditEventEvents.OnUpdateDatePicker(selectedDate,
                        selectedMonth,
                        selectedYear))
                }
            }

            datePickerFragment.isCancelable = false
            datePickerFragment.show(supportFragmentManager, "DatePickerFragment")

        }

        binding.reminderPicker.setOnClickListener {
            val reminderPickerFragment = ReminderFragment()
            val supportFragmentManager = requireActivity().supportFragmentManager

            supportFragmentManager.setFragmentResultListener(
                "REMINDER_PICKER_RESULT",
                viewLifecycleOwner
            ) {resultKey, bundle ->
                if(resultKey == "REMINDER_PICKER_RESULT") {
                    val reminder = bundle.getStringArrayList("SELECTED_REMINDERS")
                    val joinToString = reminder?.joinToString(", ")
                    binding.reminderPicker.setText(joinToString)

                    viewModel.onTriggerEvent(EditEventEvents.OnUpdateReminderPicker(joinToString!!))
                }
            }

            reminderPickerFragment.isCancelable = false
            reminderPickerFragment.show(supportFragmentManager, "ReminderPickerFragment")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}