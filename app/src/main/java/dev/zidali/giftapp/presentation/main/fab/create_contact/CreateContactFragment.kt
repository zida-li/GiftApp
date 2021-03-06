package dev.zidali.giftapp.presentation.main.fab.create_contact

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.zidali.giftapp.business.domain.util.StateMessageCallback
import dev.zidali.giftapp.databinding.FragmentCreateContactBinding
import dev.zidali.giftapp.presentation.update.GlobalEvents
import dev.zidali.giftapp.presentation.update.GlobalManager
import dev.zidali.giftapp.util.processQueue
import javax.inject.Inject

@AndroidEntryPoint
class CreateContactFragment : DialogFragment() {

    @Inject
    lateinit var globalManager: GlobalManager

    private val viewModel: CreateContactViewModel by viewModels()
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
        subscribeObservers()

        dialog?.window?.apply {

            setGravity(Gravity.BOTTOM)
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            attributes.y = 200
        }

        binding.createButton.setOnClickListener {
            cacheState()
            viewModel.onTriggerEvent(CreateContactEvents.CreateContact)
            val createdContact = Bundle()
            createdContact.putString("ADDED_CONTACT", viewModel.state.value?.name)
            setFragmentResult("ADD_CONTACT_RESULT", createdContact)
        }

        binding.cancelButton.setOnClickListener {
            dismiss()
        }
    }

    private fun subscribeObservers() {

        viewModel.state.observe(viewLifecycleOwner) { state ->

            if (state.createContactSuccessful) {
                globalManager.onTriggerEvent(GlobalEvents.SetNeedToUpdateContact(true))
                dismiss()
            }

            processQueue(
                context = context,
                queue = state.queue,
                stateMessageCallback = object : StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.onTriggerEvent(CreateContactEvents.OnRemoveHeadFromQueue)
                    }
                }
            )

        }
    }

    private fun cacheState() {
        viewModel.onTriggerEvent(CreateContactEvents.OnUpdateName(binding.inputName.text.toString()))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}