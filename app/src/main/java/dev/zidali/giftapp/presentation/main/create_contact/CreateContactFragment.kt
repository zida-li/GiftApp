package dev.zidali.giftapp.presentation.main.create_contact

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.zidali.giftapp.business.domain.util.StateMessageCallback
import dev.zidali.giftapp.databinding.FragmentCreateContactBinding
import dev.zidali.giftapp.util.Constants.Companion.TAG
import dev.zidali.giftapp.util.processQueue

@AndroidEntryPoint
class CreateContactFragment : DialogFragment() {

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
        }

        binding.cancelButton.setOnClickListener {
            dismiss()
        }
    }

    private fun subscribeObservers() {

        viewModel.state.observe(viewLifecycleOwner, { state->

            processQueue(
                context = context,
                queue = state.queue,
                stateMessageCallback = object: StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.onTriggerEvent(CreateContactEvents.OnRemoveHeadFromQueue)
                    }
                }
            )

        })
    }

    private fun cacheState() {
        viewModel.onTriggerEvent(CreateContactEvents.OnUpdateName(binding.inputName.text.toString()))
    }
}