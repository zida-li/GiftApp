package dev.zidali.giftapp.presentation.main.fab.create_event

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import dev.zidali.giftapp.databinding.FragmentReminderBinding

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

        dialog?.window?.apply {

            setGravity(Gravity.BOTTOM)
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            attributes.y = 200
        }

        binding.cancelButton.setOnClickListener {
            dismiss()
        }

        binding.oneDayCheckbox.setOnCheckedChangeListener{_, isChecked->
            if(isChecked) {
                selectedReminders.add("day")
            } else {
                selectedReminders.remove("day")
            }
        }

        binding.oneWeekCheckbox.setOnCheckedChangeListener{_, isChecked->
            if(isChecked) {
                selectedReminders.add("week")
            } else {
                selectedReminders.remove("week")
            }
        }

        binding.oneMonthCheckbox.setOnCheckedChangeListener{_, isChecked->
            if(isChecked) {
                selectedReminders.add("month")
            } else {
                selectedReminders.remove("month")
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