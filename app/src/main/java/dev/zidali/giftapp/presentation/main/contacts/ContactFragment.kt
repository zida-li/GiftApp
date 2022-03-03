package dev.zidali.giftapp.presentation.main.contacts

import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dev.zidali.giftapp.R
import dev.zidali.giftapp.business.domain.models.Contact
import dev.zidali.giftapp.business.domain.util.*
import dev.zidali.giftapp.databinding.FragmentContactsBinding
import dev.zidali.giftapp.presentation.main.BaseMainFragment
import dev.zidali.giftapp.presentation.main.MainActivity
import dev.zidali.giftapp.presentation.update.GlobalEvents
import dev.zidali.giftapp.util.processQueue

class ContactFragment : BaseMainFragment(),
ContactListAdapter.Interaction
{

    private var recyclerAdapter: ContactListAdapter? = null
    private val viewModel: ContactViewModel by viewModels()
    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!

    override fun onResume() {
        super.onResume()
        viewModel.onTriggerEvent(ContactEvents.ResetContactName)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        subscribeObservers()
        initRecyclerView()
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as MainActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24)
    }


    private fun subscribeObservers() {

        globalManager.state.observe(viewLifecycleOwner) { state ->
            if (state.needToUpdateContact) {
                viewModel.onTriggerEvent(ContactEvents.FetchContacts(
                    sessionManager.state.value?.accountProperties!!.current_authUser_email,
                    requireContext()
                ))
                globalManager.onTriggerEvent(GlobalEvents.SetNeedToUpdateContact(false))
            }
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->

            uiCommunicationListener.displayProgressBar(state.isLoading)

            recyclerAdapter?.apply {
                submitList(list = state.contactList)
            }

            if (state.firstLoad) {
                viewModel.onTriggerEvent(ContactEvents.SetFirstLoad(false))
                globalManager.onTriggerEvent(GlobalEvents.SetNeedToUpdateContact(true))
            }

            viewModel.toolbarState.observe(viewLifecycleOwner) {toolbarState->

                when(toolbarState) {

                    is ContactToolbarState.MultiSelectionState -> {
                        activity?.invalidateOptionsMenu()
                    }
                    is ContactToolbarState.RegularState -> {
                        viewModel.onTriggerEvent(ContactEvents.ClearSelectedContacts)
                        activity?.invalidateOptionsMenu()
                    }
                }

            }

            processQueue(
                context = context,
                queue = state.queue,
                stateMessageCallback = object : StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.onTriggerEvent(ContactEvents.OnRemoveHeadFromQueue)
                    }
                }
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if(isMultiSelectionModeEnabled()) {
            inflater.inflate(R.menu.multiselection_menu, menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_delete -> {
                confirmDeleteRequest()
            }
            R.id.action_exit_multiSelection -> {
                viewModel.onTriggerEvent(ContactEvents.SetToolBarState(ContactToolbarState.RegularState))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * RecyclerView
     */

    private fun initRecyclerView(){
        binding.contactRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@ContactFragment.context)
//            val topSpacingDecorator = TopSpacingItemDecoration(30)
//            removeItemDecoration(topSpacingDecorator)
//            addItemDecoration(topSpacingDecorator)
            setBackgroundColor(context.getColor(R.color.background_color_primary))
            recyclerAdapter = ContactListAdapter(
                this@ContactFragment,
                viewLifecycleOwner,
                viewModel.contactListInteractionManager.selectedContacts
            )
            adapter = recyclerAdapter
        }
    }

    /**
     * Contact Functions
     */

    private fun confirmDeleteRequest() {
        val callback: AreYouSureCallback = object: AreYouSureCallback {

            override fun proceed() {
                viewModel.onTriggerEvent(ContactEvents.DeleteSelectedContacts)
                recyclerAdapter?.notifyDataSetChanged()
            }

            override fun cancel() {
                //do nothing
            }
        }
        viewModel.onTriggerEvent(ContactEvents.AppendToMessageQueue(
            stateMessage = StateMessage(
                response = Response(
                    message = "Are You Sure? This cannot be undone",
                    uiComponentType = UIComponentType.AreYouSureDialog(callback),
                    messageType = MessageType.Info
                )
            )
        ))

    }

    override fun onItemSelected(position: Int, item: Contact) {
        if (isMultiSelectionModeEnabled()) {
            viewModel.onTriggerEvent(ContactEvents.AddOrRemoveContactFromSelectedList(item))
        } else {
            try {
                viewModel.state.value?.let {
                    val bundle = Bundle()
                    bundle.putString("selectedContact", item.contact_name)
                    bundle.putInt("selectedContactPk", item.contact_pk!!)
                    viewModel.onTriggerEvent(ContactEvents.PassDataToViewPager(item.contact_name!!, item.contact_pk!!))
                    findNavController().navigate(R.id.action_contactFragment_to_contactDetailFragment,
                        bundle)
                } ?: throw Exception("Null Contact")
            } catch (e: Exception) {
                ContactEvents.AppendToMessageQueue(
                    stateMessage = StateMessage(
                        response = Response(
                            message = e.message,
                            uiComponentType = UIComponentType.Dialog,
                            messageType = MessageType.Error,
                        )
                    )
                )
            }
        }
    }

    override fun activateMultiSelectionMode() {
        viewModel.onTriggerEvent(ContactEvents.SetToolBarState(ContactToolbarState.MultiSelectionState))
    }

    override fun isMultiSelectionModeEnabled(): Boolean {
        return viewModel.contactListInteractionManager.isMultiSelectionStateActive()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}