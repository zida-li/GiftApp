package dev.zidali.giftapp.presentation.main.contacts.contact_detail

import android.os.Bundle
import android.text.InputType
import android.view.*
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayoutMediator
import dev.zidali.giftapp.R
import dev.zidali.giftapp.business.domain.util.StateMessageCallback
import dev.zidali.giftapp.databinding.FragmentContactDetailBinding
import dev.zidali.giftapp.presentation.main.BaseMainFragment
import dev.zidali.giftapp.presentation.main.contacts.contact_detail.event.EventEvents
import dev.zidali.giftapp.presentation.main.contacts.contact_detail.event.EventToolbarState
import dev.zidali.giftapp.presentation.update.GlobalEvents
import dev.zidali.giftapp.util.processQueue

class ContactDetailFragment : BaseMainFragment() {

    private val viewModel: ContactDetailViewModel by viewModels()
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
        setHasOptionsMenu(true)
        initViewPager()
        subscribeObservers()
    }

    private fun subscribeObservers(){

        viewModel.state.observe(viewLifecycleOwner) { state ->


            binding.contactName.setText(state.contact_name)


            if (state.isEditing) {
                activateEditMode()
            }

            if (!state.isEditing) {
                deactivateEditMode()
            }

            processQueue(
                context = context,
                queue = state.queue,
                stateMessageCallback = object : StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.onTriggerEvent(ContactDetailEvents.OnRemoveHeadFromQueue)
                    }
                }
            )
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if(isEditModeEnabled()) {
            inflater.inflate(R.menu.contact_menu_edit, menu)
        } else {
            inflater.inflate(R.menu.contact_menu, menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId) {
            R.id.action_edit -> {
                viewModel.onTriggerEvent(ContactDetailEvents.ActivateEditMode)
            }
            R.id.action_finished -> {
                cacheState()
                globalManager.onTriggerEvent(GlobalEvents.SetNeedToUpdateContact(true))
            }
        }

        return super.onOptionsItemSelected(item)
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

    private fun activateEditMode() {

        activity?.invalidateOptionsMenu()
        val editText = binding.contactName

        editText.inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS
        editText.requestFocus()
        editText.setSelection(editText.length())
        uiCommunicationListener.showSoftKeyboard()
    }

    private fun deactivateEditMode() {
        activity?.invalidateOptionsMenu()
        binding.contactName.inputType = InputType.TYPE_NULL
    }

    private fun cacheState() {
        viewModel.onTriggerEvent(ContactDetailEvents.OnUpdateContact(binding.contactName.text.toString()))
        viewModel.onTriggerEvent(ContactDetailEvents.UpdateContact)
        viewModel.onTriggerEvent(ContactDetailEvents.UpdateTitle)
        viewModel.onTriggerEvent(ContactDetailEvents.DeactivateEditMode)
        uiCommunicationListener.hideSoftKeyboard()
    }

    private fun isEditModeEnabled(): Boolean {
        return viewModel.state.value?.isEditing!!
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}