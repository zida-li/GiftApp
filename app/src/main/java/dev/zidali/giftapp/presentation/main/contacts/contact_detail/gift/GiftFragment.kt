package dev.zidali.giftapp.presentation.main.contacts.contact_detail.gift

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import dev.zidali.giftapp.databinding.FragmentGiftBinding
import dev.zidali.giftapp.presentation.main.BaseMainFragment
import dev.zidali.giftapp.presentation.main.MainActivity

class GiftFragment : BaseMainFragment() {

    private val viewModel: GiftViewModel by viewModels()
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

        viewModel.onTriggerEvent(GiftEvents.FetchContactName)
        (activity as MainActivity).supportActionBar?.title = viewModel.state.value?.contact_name
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}