package dev.zidali.giftapp.presentation.main.contacts.contactDetail

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import dev.zidali.giftapp.presentation.main.contacts.contactDetail.events.EventsFragment
import dev.zidali.giftapp.presentation.main.contacts.contactDetail.gift.GiftFragment

class FragmentAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun createFragment(position: Int): Fragment {
        when(position) {
            0 -> return GiftFragment()
            1 -> return EventsFragment()
        }
        return GiftFragment()
    }

    override fun getItemCount(): Int {
        return 2
    }
}