package dev.zidali.giftapp.presentation.auth.launcher

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.launch
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dev.zidali.giftapp.R
import dev.zidali.giftapp.business.datasource.network.auth.LoginWithGoogleActivityResult
import dev.zidali.giftapp.databinding.FragmentLauncherBinding
import dev.zidali.giftapp.presentation.auth.BaseAuthFragment

class LauncherFragment: BaseAuthFragment() {

    private val viewModel: LauncherViewModel by viewModels()
    private var _binding: FragmentLauncherBinding? = null
    private val binding get() = _binding!!

    private val googleLoginLauncher = registerForActivityResult(LoginWithGoogleActivityResult()) {
        it ?: return@registerForActivityResult
        viewModel.onTriggerEvent(LauncherEvents.LoginWithGoogle(it))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLauncherBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginButton.setOnClickListener {
            navLogin()
        }

        binding.registerButton.setOnClickListener{
            navRegistration()
        }

        binding.loginWithGoogle.setOnClickListener {
            loginWithGoogle()
        }

    }

    private fun navRegistration() {
        findNavController().navigate(R.id.action_launcherFragment_to_registerFragment)
    }

    private fun navLogin() {
        findNavController().navigate(R.id.action_launcherFragment_to_loginFragment)
    }

    private fun loginWithGoogle() {
        googleLoginLauncher.launch()
    }

}