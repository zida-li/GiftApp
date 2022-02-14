package dev.zidali.giftapp.presentation.main.fab.create_event

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import dev.zidali.giftapp.databinding.FragmentReminderBinding
import dev.zidali.giftapp.util.Constants.Companion.TAG

class ReminderFragment: DialogFragment() {

    private var _binding: FragmentReminderBinding? = null
    private val binding get() = _binding!!

    private val selectedReminders: ArrayList<String> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentReminderBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = arguments
        if(bundle != null) {
            if (bundle.getString("REMINDER_SELECTION")?.contains("day", true)!!) {
                selectedReminders.add("day")
                binding.oneDayCheckbox.isChecked = true
                selectedReminders.remove("None")
            }
            if (bundle.getString("REMINDER_SELECTION")?.contains("week", true)!!) {
                selectedReminders.add("week")
                binding.oneWeekCheckbox.isChecked = true
                selectedReminders.remove("None")
            }
            if (bundle.getString("REMINDER_SELECTION")?.contains("month", true)!!) {
                selectedReminders.add("month")
                binding.oneMonthCheckbox.isChecked = true
                selectedReminders.remove("None")
            }
        } else {
            binding.noneRadioButton.isChecked = true
            selectedReminders.add("None")
        }

        dialog?.window?.apply {

            setGravity(Gravity.BOTTOM)
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            attributes.y = 200
        }

        binding.cancelButton.setOnClickListener {
            selectedReminders.clear()
            selectedReminders.add("None")
            val selectedRemindersBundle = Bundle()
            selectedRemindersBundle.putStringArrayList("SELECTED_REMINDERS", selectedReminders)
            setFragmentResult("REMINDER_PICKER_RESULT", selectedRemindersBundle)
            dismiss()
        }

        binding.oneDayCheckbox.setOnCheckedChangeListener{_, isChecked->
            if(isChecked) {
                selectedReminders.add("Day")
                binding.noneRadioButton.isChecked = false
                selectedReminders.remove("None")
            } else {
                selectedReminders.remove("Day")
            }
        }

        binding.oneWeekCheckbox.setOnCheckedChangeListener{_, isChecked->
            if(isChecked) {
                selectedReminders.add("Week")
                binding.noneRadioButton.isChecked = false
                selectedReminders.remove("None")
            } else {
                selectedReminders.remove("Week")
            }
        }

        binding.oneMonthCheckbox.setOnCheckedChangeListener{_, isChecked->
            if(isChecked) {
                selectedReminders.add("Month")
                binding.noneRadioButton.isChecked = false
                selectedReminders.remove("None")
            } else {
                selectedReminders.remove("Month")
            }
        }

        binding.noneRadioButton.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) {
                binding.oneDayCheckbox.isChecked = false
                binding.oneWeekCheckbox.isChecked = false
                binding.oneMonthCheckbox.isChecked = false
                selectedReminders.clear()
                selectedReminders.add("None")
            }
        }

        binding.createButton.setOnClickListener {
            val selectedRemindersBundle = Bundle()
            selectedRemindersBundle.putStringArrayList("SELECTED_REMINDERS", selectedReminders)
            setFragmentResult("REMINDER_PICKER_RESULT", selectedRemindersBundle)
            dismiss()
        }

    }
}