package dev.zidali.giftapp.presentation.main.contacts.contact_detail

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import dev.zidali.giftapp.presentation.main.contacts.contact_detail.event.EventFragment
import dev.zidali.giftapp.presentation.main.contacts.contact_detail.gift.GiftFragment

class FragmentAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun createFragment(position: Int): Fragment {
        when(position) {
            0 -> return GiftFragment()
            1 -> return EventFragment()
        }
        return GiftFragment()
    }

    override fun getItemCount(): Int {
        return 2
    }
}