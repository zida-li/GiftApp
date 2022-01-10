package dev.zidali.giftapp.presentation.main.fab.create_event

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.zidali.giftapp.util.Constants.Companion.TAG
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    private val calendar = Calendar.getInstance()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(requireActivity(), this, year, month, day)
    }

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {

        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        val userSelection = SimpleDateFormat("MM-dd-yyyy", Locale.ENGLISH).format(calendar.time)

        val selectedDateBundle = Bundle()
        selectedDateBundle.putString("USER_SELECTION", userSelection)
        selectedDateBundle.putInt("SELECTED_YEAR", year)
        selectedDateBundle.putInt("SELECTED_MONTH", month)
        selectedDateBundle.putInt("SELECTED_DATE", dayOfMonth)

        setFragmentResult("DATE_PICKER_RESULT", selectedDateBundle)
    }

}