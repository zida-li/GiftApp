package dev.zidali.giftapp.presentation.main.create_contact

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import dev.zidali.giftapp.databinding.FragmentCreateContactBinding

class CreateContactFragment: DialogFragment() {

    private var _binding: FragmentCreateContactBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCreateContactBinding.inflate(layoutInflater)
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

            datePickerFragment.show(supportFragmentManager, "DatePickerFragment")

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}