package dev.zidali.giftapp.presentation.main.all_events

import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dev.zidali.giftapp.R
import dev.zidali.giftapp.business.domain.models.ContactEvent
import dev.zidali.giftapp.business.domain.util.*
import dev.zidali.giftapp.databinding.FragmentAllEventsBinding
import dev.zidali.giftapp.presentation.main.BaseMainFragment
import dev.zidali.giftapp.presentation.main.fab.create_event.ReminderFragment
import dev.zidali.giftapp.presentation.notification.AlarmScheduler
import dev.zidali.giftapp.util.TopSpacingItemDecoration
import dev.zidali.giftapp.util.processQueue

class AllEventFragment : BaseMainFragment(),
AllEventListAdapter.Interaction {

    private val viewModel: AllEventViewModel by viewModels()
    private var recyclerAdapter: AllEventListAdapter? = null
    private var _binding: FragmentAllEventsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllEventsBinding.inflate(layoutInflater)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        subscribeObservers()
        initRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onTriggerEvent(AllEventEvents.FetchEvents)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun subscribeObservers() {

        viewModel.state.observe(viewLifecycleOwner) { state ->

            recyclerAdapter?.apply {
                submitList(list = state.contact_events)
            }

            viewModel.toolbarState.observe(viewLifecycleOwner) {toolbarState->

                when(toolbarState) {

                    is AllEventToolbarState.MultiSelectionState -> {
                        activity?.invalidateOptionsMenu()
                    }
                    is AllEventToolbarState.RegularState -> {
                        viewModel.onTriggerEvent(AllEventEvents.ClearSelectedContactEvents)
                        activity?.invalidateOptionsMenu()
                    }
                }

            }

            processQueue(
                context = context,
                queue = state.queue,
                stateMessageCallback = object : StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.onTriggerEvent(AllEventEvents.OnRemoveHeadFromQueue)
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
                viewModel.onTriggerEvent(AllEventEvents.SetToolBarState(AllEventToolbarState.RegularState))
            }
        }

        return super.onOptionsItemSelected(item)
    }

    /**
     * RecyclerView
     */

    private fun initRecyclerView() {
        binding.allEventsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@AllEventFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(30)
            removeItemDecoration(topSpacingDecorator)
            addItemDecoration(topSpacingDecorator)
            recyclerAdapter = AllEventListAdapter(
                this@AllEventFragment,
                viewLifecycleOwner,
                viewModel.allEventListInteractionManager.selectedEvents,
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
                viewModel.onTriggerEvent(AllEventEvents.DeleteSelectedContactEvents)
                recyclerAdapter?.notifyDataSetChanged()
            }

            override fun cancel() {
                //do nothing
            }
        }
        viewModel.onTriggerEvent(AllEventEvents.AppendToMessageQueue(
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
            viewModel.onTriggerEvent(AllEventEvents.AddOrRemoveContactEventFromSelectedList(item))
        } else {
            viewModel.state.value?.let { state->
                val bundle = bundleOf()
                bundle.putString("CONTACT_NAME", item.contact_name)
                bundle.putString("CONTACT_EVENT", item.contact_event)
                findNavController().navigate(R.id.action_eventsFragment_to_eventDetailFragment, bundle)
            }
        }
    }

    override fun activateMultiSelectionMode() {
        viewModel.onTriggerEvent(AllEventEvents.SetToolBarState(AllEventToolbarState.MultiSelectionState))
    }

    override fun isMultiSelectionModeEnabled(): Boolean {
        return viewModel.allEventListInteractionManager.isMultiSelectionStateActive()
    }

    override fun turnOffNotifications(item: ContactEvent) {
        viewModel.onTriggerEvent(AllEventEvents.TurnOffNotifications(item))
        AlarmScheduler.cancelScheduledAlarmForReminder(requireContext(), item, "day")
        AlarmScheduler.cancelScheduledAlarmForReminder(requireContext(), item, "week")
        AlarmScheduler.cancelScheduledAlarmForReminder(requireContext(), item, "month")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun turnOnNotifications(item: ContactEvent, position: Int) {

        val reminderPickerFragment = ReminderFragment()
        val supportFragmentManager = requireActivity().supportFragmentManager

        supportFragmentManager.setFragmentResultListener(
            "REMINDER_PICKER_RESULT",
            viewLifecycleOwner
        ) {resultKey, bundle ->
            if(resultKey == "REMINDER_PICKER_RESULT") {
                val reminder = bundle.getStringArrayList("SELECTED_REMINDERS")
                val joinToString = reminder?.joinToString(", ")

                viewModel.onTriggerEvent(AllEventEvents.TurnOnNotifications(item, joinToString!!))
                viewModel.onTriggerEvent(AllEventEvents.SetContactHolder(item, joinToString))
                AlarmScheduler.scheduleInitialAlarmsForReminder(requireContext(), viewModel.state.value?.contact_event_holder!!)
                recyclerAdapter?.notifyItemChanged(position)
            }
        }

        reminderPickerFragment.isCancelable = false
        reminderPickerFragment.show(supportFragmentManager, "ReminderPickerFragment")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}