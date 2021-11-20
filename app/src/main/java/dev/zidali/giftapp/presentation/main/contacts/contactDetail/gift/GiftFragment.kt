package dev.zidali.giftapp.presentation.main.contacts.contactDetail.gift

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dev.zidali.giftapp.databinding.FragmentGiftBinding
import dev.zidali.giftapp.presentation.main.BaseMainFragment

class GiftFragment : BaseMainFragment() {

    private var _binding: FragmentGiftBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGiftBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}