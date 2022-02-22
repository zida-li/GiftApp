package dev.zidali.giftapp.presentation.main.contacts.contact_detail.event

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dev.zidali.giftapp.R
import dev.zidali.giftapp.business.domain.models.ContactEvent
import dev.zidali.giftapp.business.domain.util.*
import dev.zidali.giftapp.databinding.FragmentEventsBinding
import dev.zidali.giftapp.presentation.edit.EditEventActivity
import dev.zidali.giftapp.presentation.main.BaseMainFragment
import dev.zidali.giftapp.presentation.main.fab.create_event.ReminderFragment
import dev.zidali.giftapp.presentation.notification.AlarmScheduler
import dev.zidali.giftapp.presentation.update.GlobalEvents
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
        globalManager.onTriggerEvent(GlobalEvents.SetEventFragmentInView(true))
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
                globalManager.onTriggerEvent(GlobalEvents.SetNeedToUpdateEventFragment(false))
                viewModel.onTriggerEvent(EventEvents.FetchContactPk)
                viewModel.onTriggerEvent(EventEvents.FetchEvents)
            }
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->

            uiCommunicationListener.displayProgressBar(state.isLoading)

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
                        globalManager.onTriggerEvent(GlobalEvents.SetMultiSelection(true))
                        activity?.invalidateOptionsMenu()
                    }
                    is EventToolbarState.RegularState -> {
                        viewModel.onTriggerEvent(EventEvents.ClearSelectedContactEvents)
                        globalManager.onTriggerEvent(GlobalEvents.SetMultiSelection(false))
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
//            val topSpacingDecorator = TopSpacingItemDecoration(30)
//            removeItemDecoration(topSpacingDecorator)
//            addItemDecoration(topSpacingDecorator)
            setBackgroundColor(context.getColor(R.color.background_color_primary))
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
        } else {
            viewModel.state.value?.let { state->
                val intent = Intent(requireContext(), EditEventActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.putExtra("CONTACT_PK", item.pk)
                intent.putExtra("EVENT_PK", item.event_pk)
                startActivity(intent)
            }
        }
    }

    override fun activateMultiSelectionMode() {
        viewModel.onTriggerEvent(EventEvents.SetToolBarState(EventToolbarState.MultiSelectionState))
    }

    override fun isMultiSelectionModeEnabled(): Boolean {
        return viewModel.eventListInteractionManager.isMultiSelectionStateActive()
    }

    override fun turnOffNotifications(item: ContactEvent) {
        viewModel.onTriggerEvent(EventEvents.TurnOffNotifications(item))
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

                viewModel.onTriggerEvent(EventEvents.TurnOnNotifications(item, joinToString!!))
                viewModel.onTriggerEvent(EventEvents.SetContactHolder(item, joinToString))
                AlarmScheduler.scheduleInitialAlarmsForReminder(requireContext(), viewModel.state.value?.contact_event_holder!!)
                recyclerAdapter?.notifyItemChanged(position)
            }
        }

        reminderPickerFragment.isCancelable = false
        reminderPickerFragment.show(supportFragmentManager, "ReminderPickerFragment")
    }

    /**
     * Others
     */

    override fun onPause() {
        super.onPause()
        globalManager.onTriggerEvent(GlobalEvents.SetEventFragmentInView(false))
        viewModel.onTriggerEvent(EventEvents.SetToolBarState(EventToolbarState.RegularState))
        uiCommunicationListener.hideSoftKeyboard()
//        Log.d(TAG, "EventFragment onPause()")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        globalManager.onTriggerEvent(GlobalEvents.SetEventFragmentInView(false))
//        Log.d(TAG, "EventFragment onDestroyView()")
    }
}