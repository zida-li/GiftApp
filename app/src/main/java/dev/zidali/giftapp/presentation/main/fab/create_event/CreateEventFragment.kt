package dev.zidali.giftapp.presentation.main.fab.create_event

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.zidali.giftapp.R
import dev.zidali.giftapp.business.domain.util.StateMessageCallback
import dev.zidali.giftapp.databinding.FragmentCreateEventBinding
import dev.zidali.giftapp.presentation.update.GlobalEvents
import dev.zidali.giftapp.presentation.update.GlobalManager
import dev.zidali.giftapp.util.Constants.Companion.TAG
import dev.zidali.giftapp.util.processQueue
import javax.inject.Inject

@AndroidEntryPoint
class CreateEventFragment: DialogFragment() {

    private val viewModel: CreateEventViewModel by viewModels()
    private var _binding: FragmentCreateEventBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var globalManager: GlobalManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCreateEventBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.onTriggerEvent(CreateEventEvents.FetchCurrentContact)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeObservers()
        viewModel.onTriggerEvent(CreateEventEvents.FetchContacts)
        setUpWindow()
        setUpOnClickListeners()

    }

    private fun subscribeObservers() {

        viewModel.state.observe(viewLifecycleOwner, { state->

            val arrayAdapter = ArrayAdapter(requireContext(), R.layout.contact_drop_down_item, state.contacts)
            binding.contactDropDownMenu.setAdapter(arrayAdapter)

            if(globalManager.state.value?.eventFragmentInView!! && !state.dataLoaded) {
                if(state.current_contact_name != "") {
                    state.contacts.add(0, state.current_contact_name)
                    binding.contactDropDownMenu.setText(state.current_contact_name)
                    viewModel.onTriggerEvent(CreateEventEvents.SetDataLoaded(true))
                }
            }

            //cacheState() does the same thing, will leave this commented out for now.
//            binding.contactDropDownMenu.setOnItemClickListener { _, _, position, _ ->
//                val selectedContact = arrayAdapter.getItem(position)
//
//            }

            if(state.addEventSuccessful) {
                globalManager.onTriggerEvent(GlobalEvents.SetNeedToUpdateEventFragment(true))
                dismiss()
            }

            processQueue(
                context = context,
                queue = state.queue,
                stateMessageCallback = object: StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.onTriggerEvent(CreateEventEvents.OnRemoveHeadFromQueue)
                    }
                }
            )

        })

    }

    private fun cacheState() {
        viewModel.onTriggerEvent(CreateEventEvents.OnUpdateContactSelection(binding.contactDropDownMenu.text.toString()))
        viewModel.onTriggerEvent(CreateEventEvents.OnUpdateEvent(binding.inputName.text.toString()))
        viewModel.onTriggerEvent(CreateEventEvents.CreateEvent)
    }

    private fun setUpWindow() {
        dialog?.window?.apply {

            setGravity(Gravity.BOTTOM)
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            attributes.y = 200
        }
    }

    private fun setUpOnClickListeners() {
        binding.datePicker.setOnClickListener {
            val datePickerFragment = DatePickerFragment()
            val supportFragmentManager = requireActivity().supportFragmentManager

            supportFragmentManager.setFragmentResultListener(
                "DATE_PICKER_RESULT",
                viewLifecycleOwner
            ) { resultKey, bundle->
                if(resultKey == "DATE_PICKER_RESULT") {
                    val userSelection = bundle.getString("USER_SELECTION")
                    binding.datePicker.setText(userSelection)

                    val selectedYear = bundle.getInt("SELECTED_YEAR")
                    val selectedMonth = bundle.getInt("SELECTED_MONTH")
                    val selectedDate = bundle.getInt("SELECTED_DATE")
                    viewModel.onTriggerEvent(CreateEventEvents.OnUpdateDatePicker(
                        selectedYear, selectedMonth, selectedDate
                    ))
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
                    viewModel.onTriggerEvent(CreateEventEvents.OnUpdateReminderPicker(joinToString!!))
                }
            }

            reminderPickerFragment.isCancelable = false
            reminderPickerFragment.show(supportFragmentManager, "ReminderPickerFragment")
        }

        binding.createButton.setOnClickListener {
            cacheState()
        }

        binding.cancelButton.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}