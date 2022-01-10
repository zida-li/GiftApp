package dev.zidali.giftapp.presentation.main.contacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dev.zidali.giftapp.R
import dev.zidali.giftapp.business.domain.models.Contact
import dev.zidali.giftapp.business.domain.util.*
import dev.zidali.giftapp.databinding.FragmentContactsBinding
import dev.zidali.giftapp.presentation.main.BaseMainFragment
import dev.zidali.giftapp.presentation.main.fab.add_gift.AddGiftFragment
import dev.zidali.giftapp.presentation.main.fab.create_contact.CreateContactFragment
import dev.zidali.giftapp.presentation.main.fab.create_event.CreateEventFragment
import dev.zidali.giftapp.presentation.update.UpdateEvents
import dev.zidali.giftapp.util.TopSpacingItemDecoration
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
        subscribeObservers()
        initRecyclerView()
    }

    private fun subscribeObservers() {

        updateManager.state.observe(viewLifecycleOwner, { state->
            if(state.needToUpdate){
                viewModel.onTriggerEvent(ContactEvents.FetchContacts)
                updateManager.onTriggerEvent(UpdateEvents.UpdateComplete)
            }
        })

        viewModel.state.observe(viewLifecycleOwner, { state->

            recyclerAdapter?.apply {
                submitList(list = state.contactList)
            }

            processQueue(
                context = context,
                queue = state.queue,
                stateMessageCallback = object: StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.onTriggerEvent(ContactEvents.OnRemoveHeadFromQueue)
                    }
                }
            )

        })
    }

    /**
     * RecyclerView
     */

    private fun initRecyclerView(){
        binding.contactRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@ContactFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(30)
            removeItemDecoration(topSpacingDecorator)
            addItemDecoration(topSpacingDecorator)
            recyclerAdapter = ContactListAdapter(this@ContactFragment)
            adapter = recyclerAdapter
        }
    }

    override fun onItemSelected(position: Int, item: Contact) {
        try {
            viewModel.state.value?.let {
                viewModel.onTriggerEvent(ContactEvents.PassDataToViewPager(item.contact_name!!))
                findNavController().navigate(R.id.action_contactFragment_to_contactDetailFragment)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}