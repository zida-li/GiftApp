package dev.zidali.giftapp.presentation.main.contacts.contact_detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import dev.zidali.giftapp.databinding.FragmentContactDetailBinding
import dev.zidali.giftapp.presentation.main.BaseMainFragment

class ContactDetailFragment : BaseMainFragment() {

    private var _binding: FragmentContactDetailBinding? = null
    private val binding get() = _binding!!

    private val titleArray = arrayOf(
        "Gifts",
        "Events"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewPager()
    }

    private fun initViewPager() {

        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout

        val adapter = FragmentAdapter(
            childFragmentManager, lifecycle
        )

        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) {tab, position ->
            tab.text = titleArray[position]
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}