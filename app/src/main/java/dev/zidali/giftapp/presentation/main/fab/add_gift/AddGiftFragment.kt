package dev.zidali.giftapp.presentation.main.fab.add_gift

import android.os.Bundle
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
import dev.zidali.giftapp.databinding.FragmentAddGiftBinding
import dev.zidali.giftapp.presentation.update.GlobalEvents
import dev.zidali.giftapp.presentation.update.GlobalManager
import dev.zidali.giftapp.util.processQueue
import javax.inject.Inject

@AndroidEntryPoint
class AddGiftFragment: DialogFragment() {

    private val viewModel: AddGiftViewModel by viewModels()
    private var _binding: FragmentAddGiftBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var globalManager: GlobalManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAddGiftBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.onTriggerEvent(AddGiftEvents.FetchCurrentContact)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeObservers()

        viewModel.onTriggerEvent(AddGiftEvents.FetchContacts)

        dialog?.window?.apply {
            setGravity(Gravity.BOTTOM)
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            attributes.y = 200
        }

        binding.createButton.setOnClickListener {
            cacheState()
            viewModel.onTriggerEvent(AddGiftEvents.AddGift)
        }

        binding.cancelButton.setOnClickListener {
            dismiss()
        }
    }

    private fun subscribeObservers() {

        viewModel.state.observe(viewLifecycleOwner) { state ->

            val arrayAdapter =
                ArrayAdapter(requireContext(), R.layout.contact_drop_down_item, state.contact_display_list)
            binding.contactDropDownMenu.setAdapter(arrayAdapter)

            if (globalManager.state.value?.giftFragmentInView!! && !state.dataLoaded) {
                if (state.current_contact_name != "") {
                    state.contact_display_list.add(0, state.current_contact_name)
                    binding.contactDropDownMenu.setText(state.current_contact_name)
                    viewModel.onTriggerEvent(AddGiftEvents.SetDataLoaded(true))
                }
            }

            if (state.addGiftSuccessful) {
                globalManager.onTriggerEvent(GlobalEvents.SetNeedToUpdate(true))
                dismiss()
            }

            processQueue(
                context = context,
                queue = state.queue,
                stateMessageCallback = object : StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.onTriggerEvent(AddGiftEvents.OnRemoveHeadFromQueue)
                    }
                }
            )

        }
    }

    private fun cacheState(){
        viewModel.onTriggerEvent(AddGiftEvents.OnUpdateGift(
            contact = binding.contactDropDownMenu.text.toString(),
            gift = binding.inputGift.text.toString()
        ))
    }

    private fun resetState(){
        binding.contactDropDownMenu.setText(R.string.select_contact)
        binding.inputGift.setText("")
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}