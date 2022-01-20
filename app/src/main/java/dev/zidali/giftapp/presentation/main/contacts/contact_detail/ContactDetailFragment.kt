package dev.zidali.giftapp.presentation.main.contacts.contact_detail

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayoutMediator
import dev.zidali.giftapp.R
import dev.zidali.giftapp.business.domain.util.StateMessageCallback
import dev.zidali.giftapp.databinding.FragmentContactDetailBinding
import dev.zidali.giftapp.presentation.main.BaseMainFragment
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

        initViewPager()
        subscribeObservers()
    }

    private fun subscribeObservers(){

        viewModel.state.observe(viewLifecycleOwner, {state->


            binding.contactName.setText(state.contact_name)


            if(state.isEditing) {
                activateEditMode()
            }

            if(!state.isEditing) {
                deactivateEditMode()
            }

            processQueue(
                context = context,
                queue = state.queue,
                stateMessageCallback = object: StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.onTriggerEvent(ContactDetailEvents.OnRemoveHeadFromQueue)
                    }
                }
            )
        })

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

        val editText = binding.contactName

        binding.editButton.setImageResource(R.drawable.ic_baseline_check_24)
        editText.inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS
        editText.requestFocus()
        editText.setSelection(editText.length())
        uiCommunicationListener.showSoftKeyboard()
        binding.editButton.setOnClickListener {
            cacheState()
            viewModel.onTriggerEvent(ContactDetailEvents.UpdateContact)
            viewModel.onTriggerEvent(ContactDetailEvents.UpdateTitle)
            globalManager.onTriggerEvent(GlobalEvents.SetNeedToUpdate(true))
            viewModel.onTriggerEvent(ContactDetailEvents.DeactivateEditMode)
            uiCommunicationListener.hideSoftKeyboard()
        }
    }

    private fun deactivateEditMode() {
        binding.editButton.setOnClickListener {
            viewModel.onTriggerEvent(ContactDetailEvents.ActivateEditMode)
        }
        binding.editButton.setImageResource(R.drawable.ic_baseline_edit_24)
        binding.contactName.inputType = InputType.TYPE_NULL
    }

    private fun cacheState() {
        viewModel.onTriggerEvent(ContactDetailEvents.OnUpdateContact(binding.contactName.text.toString()))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}