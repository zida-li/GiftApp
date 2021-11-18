package dev.zidali.giftapp.presentation.auth.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import dev.zidali.giftapp.business.domain.util.StateMessageCallback
import dev.zidali.giftapp.databinding.FragmentRegisterBinding
import dev.zidali.giftapp.presentation.auth.BaseAuthFragment
import dev.zidali.giftapp.util.processQueue

class RegisterFragment: BaseAuthFragment() {

    private val viewModel: RegisterViewModel by viewModels()
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeObservers()
        restoreEmail()

        binding.loginButton.setOnClickListener {
            cacheState()
            uiCommunicationListener.hideSoftKeyboard()
            viewModel.onTriggerEvent(RegisterEvents.RegisterWithGoogle)
        }
    }

    private fun subscribeObservers() {

        viewModel.state.observe(viewLifecycleOwner, {state->

            processQueue(
                context = context,
                queue = state.queue,
                stateMessageCallback = object: StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.onTriggerEvent(RegisterEvents.OnRemoveHeadFromQueue)
                    }
                }
            )

        })

    }

    private fun cacheState(){
        viewModel.onTriggerEvent(RegisterEvents.OnUpdateEmail(binding.inputEmail.text.toString()))
        viewModel.onTriggerEvent(RegisterEvents.OnUpdatePassword(binding.inputPassword.text.toString()))
        viewModel.onTriggerEvent(RegisterEvents.OnUpdateConfirmPassword(binding.inputConfirmPassword.text.toString()))
    }

    private fun restoreEmail() {
        viewModel.state.let { state ->
            state.value?.registration_email?.let { binding.inputEmail.setText(it) }
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        cacheState()
        viewModel.onTriggerEvent(RegisterEvents.SaveRegisterState)
        super.onPause()
    }
}