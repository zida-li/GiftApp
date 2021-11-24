package dev.zidali.giftapp.presentation.auth.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import dev.zidali.giftapp.business.domain.util.StateMessageCallback
import dev.zidali.giftapp.databinding.FragmentLoginBinding
import dev.zidali.giftapp.presentation.auth.BaseAuthFragment
import dev.zidali.giftapp.util.processQueue

class LoginFragment: BaseAuthFragment() {

    private val viewModel: LoginViewModel by viewModels()
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeObservers()
        restoreEmail()

        binding.loginButton.setOnClickListener {
            cacheState()
            uiCommunicationListener.hideSoftKeyboard()
            viewModel.onTriggerEvent(LoginEvents.LoginWithGoogle)
        }
    }

    private fun subscribeObservers() {

        viewModel.state.observe(viewLifecycleOwner, { state->

            uiCommunicationListener.displayProgressBar(state.isLoading)

            processQueue(
                context = context,
                queue = state.queue,
                stateMessageCallback = object: StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.onTriggerEvent(LoginEvents.OnRemoveHeadFromQueue)
                    }
                }
            )

        })

    }

    private fun cacheState() {
        viewModel.onTriggerEvent(LoginEvents.OnUpdateEmail(binding.inputEmail.text.toString()))
        viewModel.onTriggerEvent(LoginEvents.OnUpdatePassword(binding.inputPassword.text.toString()))
    }

    private fun restoreEmail() {
        viewModel.state.let { state ->
            state.value?.login_email?.let { binding.inputEmail.setText(it) }
        }
    }

    override fun onPause() {
        cacheState()
        viewModel.onTriggerEvent(LoginEvents.SaveLoginState)
        super.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}