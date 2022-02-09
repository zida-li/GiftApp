package dev.zidali.giftapp.presentation.main.contacts.contact_detail.event

import android.app.AlarmManager
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dev.zidali.giftapp.R
import dev.zidali.giftapp.business.domain.models.ContactEvent
import dev.zidali.giftapp.business.domain.util.*
import dev.zidali.giftapp.databinding.FragmentEventsBinding
import dev.zidali.giftapp.presentation.main.BaseMainFragment
import dev.zidali.giftapp.presentation.main.contacts.ContactEvents
import dev.zidali.giftapp.presentation.main.contacts.ContactToolbarState
import dev.zidali.giftapp.presentation.update.GlobalEvents
import dev.zidali.giftapp.util.Constants.Companion.TAG
import dev.zidali.giftapp.util.TopSpacingItemDecoration
import dev.zidali.giftapp.util.processQueue

class EventFragment : BaseMainFragment(),
EventListAdapter.Interaction {

    private val viewModel: EventViewModel by viewModels()
    private var recyclerAdapter: EventListAdapter? = null
    private var _binding: FragmentEventsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        globalManager.onTriggerEvent(GlobalEvents.EventFragmentInView(true))
//        Log.d(Constants.TAG, "EventFragment onResume()")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        subscribeObservers()
        initRecyclerView()
    }

    private fun subscribeObservers() {

        globalManager.state.observe(viewLifecycleOwner) { state ->

            if (state.needToUpdateEventFragment) {
                viewModel.onTriggerEvent(EventEvents.FetchContactName)
                viewModel.onTriggerEvent(EventEvents.FetchEvents)
                globalManager.onTriggerEvent(GlobalEvents.SetNeedToUpdateEventFragment(false))
            }
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->

            recyclerAdapter?.apply {
                submitList(list = state.contact_events)
            }

            if (state.firstLoad) {
                viewModel.onTriggerEvent(EventEvents.SetFirstLoad(false))
                globalManager.onTriggerEvent(GlobalEvents.SetNeedToUpdateEventFragment(true))
            }

            viewModel.toolbarState.observe(viewLifecycleOwner) {toolbarState->

                when(toolbarState) {

                    is EventToolbarState.MultiSelectionState -> {
                        activity?.invalidateOptionsMenu()
                    }
                    is EventToolbarState.RegularState -> {
                        viewModel.onTriggerEvent(EventEvents.ClearSelectedContactEvents)
                        activity?.invalidateOptionsMenu()
                    }
                }

            }

            processQueue(
                context = context,
                queue = state.queue,
                stateMessageCallback = object : StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.onTriggerEvent(EventEvents.OnRemoveHeadFromQueue)
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
                viewModel.onTriggerEvent(EventEvents.SetToolBarState(EventToolbarState.RegularState))
            }
        }

        return super.onOptionsItemSelected(item)
    }

    /**
     * RecyclerView
     */

    private fun initRecyclerView() {
        binding.eventsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@EventFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(30)
            removeItemDecoration(topSpacingDecorator)
            addItemDecoration(topSpacingDecorator)
            recyclerAdapter = EventListAdapter(
                this@EventFragment,
                viewLifecycleOwner,
                viewModel.eventListInteractionManager.selectedContactEvents
            )
            adapter = recyclerAdapter
        }
    }

    /**
     * Event Functions
     */

    private fun confirmDeleteRequest() {
        val callback: AreYouSureCallback = object: AreYouSureCallback {

            override fun proceed() {
                viewModel.onTriggerEvent(EventEvents.DeleteSelectedContactEvents)
                recyclerAdapter?.notifyDataSetChanged()
            }

            override fun cancel() {
                //do nothing
            }
        }
        viewModel.onTriggerEvent(EventEvents.AppendToMessageQueue(
            stateMessage = StateMessage(
                response = Response(
                    message = "Are You Sure? This cannot be undone",
                    uiComponentType = UIComponentType.AreYouSureDialog(callback),
                    messageType = MessageType.Info
                )
            )
        ))

    }

    override fun onItemSelected(position: Int, item: ContactEvent) {
        if(isMultiSelectionModeEnabled()) {
            viewModel.onTriggerEvent(EventEvents.AddOrRemoveContactEventFromSelectedList(item))
        }
    }

    override fun activateMultiSelectionMode() {
        viewModel.onTriggerEvent(EventEvents.SetToolBarState(EventToolbarState.MultiSelectionState))
    }

    override fun isMultiSelectionModeEnabled(): Boolean {
        return viewModel.eventListInteractionManager.isMultiSelectionStateActive()
    }

    /**
     * Others
     */

    override fun onPause() {
        super.onPause()
        globalManager.onTriggerEvent(GlobalEvents.EventFragmentInView(false))
//        Log.d(TAG, "EventFragment onPause()")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        globalManager.onTriggerEvent(GlobalEvents.EventFragmentInView(false))
//        Log.d(TAG, "EventFragment onDestroyView()")
    }
}