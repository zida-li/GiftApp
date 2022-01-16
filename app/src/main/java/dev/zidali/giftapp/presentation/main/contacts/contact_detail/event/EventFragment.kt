package dev.zidali.giftapp.presentation.main.contacts.contact_detail.event

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dev.zidali.giftapp.business.domain.models.ContactEvent
import dev.zidali.giftapp.business.domain.util.StateMessageCallback
import dev.zidali.giftapp.databinding.FragmentEventsBinding
import dev.zidali.giftapp.presentation.main.BaseMainFragment
import dev.zidali.giftapp.presentation.main.contacts.contact_detail.gift.GiftListAdapter
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
        viewModel.onTriggerEvent(EventEvents.FetchContactName)
        viewModel.onTriggerEvent(EventEvents.FetchEvents)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeObservers()
        initRecyclerView()
    }

    private fun subscribeObservers() {

        viewModel.state.observe(viewLifecycleOwner, {state->

            recyclerAdapter?.apply {
                submitList(list = state.contact_events)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemSelected(position: Int, item: ContactEvent) {
        TODO("Not yet implemented")
    }
}