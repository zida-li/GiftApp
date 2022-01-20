package dev.zidali.giftapp.presentation.main.contacts.contact_detail.event

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dev.zidali.giftapp.business.domain.models.ContactEvent
import dev.zidali.giftapp.business.domain.util.StateMessageCallback
import dev.zidali.giftapp.databinding.FragmentEventsBinding
import dev.zidali.giftapp.presentation.main.BaseMainFragment
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
        subscribeObservers()
        initRecyclerView()
    }

    private fun subscribeObservers() {

        globalManager.state.observe(viewLifecycleOwner, { state->

            if(state.needToUpdateEventFragment){
                viewModel.onTriggerEvent(EventEvents.FetchContactName)
                viewModel.onTriggerEvent(EventEvents.FetchEvents)
                globalManager.onTriggerEvent(GlobalEvents.SetNeedToUpdateEventFragment(false))
            }
        })

        viewModel.state.observe(viewLifecycleOwner, {state->

            recyclerAdapter?.apply {
                submitList(list = state.contact_events)
            }

            if(state.firstLoad) {
                viewModel.onTriggerEvent(EventEvents.SetFirstLoad(false))
                globalManager.onTriggerEvent(GlobalEvents.SetNeedToUpdateEventFragment(true))
            }

            processQueue(
                context = context,
                queue = state.queue,
                stateMessageCallback = object: StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.onTriggerEvent(EventEvents.OnRemoveHeadFromQueue)
                    }
                }
            )
        })
    }

    private fun initRecyclerView() {
        binding.eventsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@EventFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(30)
            removeItemDecoration(topSpacingDecorator)
            addItemDecoration(topSpacingDecorator)
            recyclerAdapter = EventListAdapter(this@EventFragment)
            adapter = recyclerAdapter
        }
    }

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

    override fun onItemSelected(position: Int, item: ContactEvent) {
        TODO("Not yet implemented")
    }
}