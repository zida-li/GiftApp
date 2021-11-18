package dev.zidali.giftapp.presentation.auth.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import dev.zidali.giftapp.databinding.FragmentLoginBinding
import dev.zidali.giftapp.presentation.auth.BaseAuthFragment

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


    }
}