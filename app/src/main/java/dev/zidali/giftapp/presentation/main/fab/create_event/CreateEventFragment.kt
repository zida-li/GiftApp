package dev.zidali.giftapp.presentation.main.fab.create_event

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import dev.zidali.giftapp.databinding.FragmentCreateEventBinding

class CreateEventFragment: DialogFragment() {

    private var _binding: FragmentCreateEventBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCreateEventBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.apply {

            setGravity(Gravity.BOTTOM)
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            attributes.y = 200
        }

        binding.datePicker.setOnClickListener {
            val datePickerFragment = DatePickerFragment()
            val supportFragmentManager = requireActivity().supportFragmentManager

            supportFragmentManager.setFragmentResultListener(
                "DATE_PICKER_RESULT",
                viewLifecycleOwner
            ) { resultKey, bundle->
                if(resultKey == "DATE_PICKER_RESULT") {
                    val date = bundle.getString("SELECTED_DATE")
                    binding.datePicker.text = date
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
                    binding.reminderPicker.text = reminder?.joinToString(", ")
                }
            }

            reminderPickerFragment.isCancelable = false
            reminderPickerFragment.show(supportFragmentManager, "ReminderPickerFragment")
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