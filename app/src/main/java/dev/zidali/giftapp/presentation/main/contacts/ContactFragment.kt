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
import dev.zidali.giftapp.presentation.main.create_contact.CreateContactFragment
import dev.zidali.giftapp.presentation.main.create_event.CreateEventFragment
import dev.zidali.giftapp.util.TopSpacingItemDecoration
import dev.zidali.giftapp.util.processQueue

class ContactFragment : BaseMainFragment(),
ContactListAdapter.Interaction
{

    //Animations
    private val rotateOpen: Animation by lazy { AnimationUtils.loadAnimation(context, R.anim.rotate_open_anim) }
    private val rotateClose: Animation by lazy { AnimationUtils.loadAnimation(context, R.anim.rotate_close_anim) }
    private val fromBottom: Animation by lazy { AnimationUtils.loadAnimation(context, R.anim.from_bottom_anim) }
    private val toBottom: Animation by lazy { AnimationUtils.loadAnimation(context, R.anim.to_bottom_anim) }
    private var clicked = false

    private var recyclerAdapter: ContactListAdapter? = null
    private val viewModel: ContactViewModel by viewModels()
    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!

    override fun onResume() {
        super.onResume()
        viewModel.onTriggerEvent(ContactEvents.FetchContacts)
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
        initOnClickListeners()
        subscribeObservers()
        initRecyclerView()
    }

    private fun subscribeObservers() {

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
                        messageType = MessageType.Error
                    )
                )
            )
        }
    }

    /**
     * OnClick Listeners
     */

    private fun initOnClickListeners() {
        binding.fabMenu.setOnClickListener {
            onMenuButtonClicked()
        }

        binding.fabAddEvent.setOnClickListener {

            val dialog = CreateEventFragment()
            val supportFragmentManager = requireActivity().supportFragmentManager

            dialog.isCancelable = false

            dialog.show(supportFragmentManager, "createContactDialog")
        }

        binding.fabAddContact.setOnClickListener {
            val createContactFragment = CreateContactFragment()
            val supportFragmentManager = requireActivity().supportFragmentManager

            supportFragmentManager.setFragmentResultListener(
                "ADD_CONTACT_RESULT",
                this
            ) {resultKey, bundle ->
                if(resultKey == "ADD_CONTACT_RESULT") {
                    val addedContact = bundle.getString("ADDED_CONTACT")
                    viewModel.onTriggerEvent(ContactEvents.AppendToMessageQueue(
                        stateMessage = StateMessage(
                            response = Response(
                                message = "$addedContact Added To Contact",
                                uiComponentType = UIComponentType.Toast,
                                messageType = MessageType.None
                            )
                        )
                    ))
                }
            }
            createContactFragment.isCancelable = false
            createContactFragment.show(supportFragmentManager, "CreateContactFragment")

        }
    }

    /**
     * FAB Menu Functions & Animations
     */
    private fun onMenuButtonClicked() {
        setVisibility(clicked)
        setAnimation(clicked)
        setClickable(clicked)
        clicked = !clicked
    }

    private fun setVisibility(clicked: Boolean) {
        if(!clicked) {
            binding.fabAddContact.visibility = View.VISIBLE
            binding.fabAddGift.visibility = View.VISIBLE
            binding.fabAddEvent.visibility = View.VISIBLE
        } else {
            binding.fabAddContact.visibility = View.INVISIBLE
            binding.fabAddGift.visibility = View.INVISIBLE
            binding.fabAddEvent.visibility = View.INVISIBLE
        }
    }

    private fun setAnimation(clicked:Boolean) {
        if(!clicked) {
            binding.fabAddContact.startAnimation(fromBottom)
            binding.fabAddGift.startAnimation(fromBottom)
            binding.fabAddEvent.startAnimation(fromBottom)
            binding.fabMenu.startAnimation(rotateOpen)
        } else {
            binding.fabAddContact.startAnimation(toBottom)
            binding.fabAddGift.startAnimation(toBottom)
            binding.fabAddEvent.startAnimation(toBottom)
            binding.fabMenu.startAnimation(rotateClose)
        }
    }

    private fun setClickable(clicked: Boolean) {
        if(!clicked) {
            binding.fabAddContact.isClickable = true
            binding.fabAddGift.isClickable = true
            binding.fabAddEvent.isClickable = true
        } else {
            binding.fabAddContact.isClickable = false
            binding.fabAddGift.isClickable = false
            binding.fabAddEvent.isClickable = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}