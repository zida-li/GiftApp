package dev.zidali.giftapp.presentation.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dev.zidali.giftapp.business.domain.models.ContactEvent
import dev.zidali.giftapp.business.domain.util.StateMessageCallback
import dev.zidali.giftapp.databinding.FragmentAllEventsBinding
import dev.zidali.giftapp.databinding.FragmentEventsBinding
import dev.zidali.giftapp.presentation.main.BaseMainFragment
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeObservers()
        initRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onTriggerEvent(AllEventEvents.FetchEvents)
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
                        viewModel.onTriggerEvent(AllEventEvents.OnRemoveHeadFromQueue)
                    }
                }
            )
        })
    }

    private fun initRecyclerView() {
        binding.allEventsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@AllEventFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(30)
            removeItemDecoration(topSpacingDecorator)
            addItemDecoration(topSpacingDecorator)
            recyclerAdapter = AllEventListAdapter(this@AllEventFragment)
            adapter = recyclerAdapter
        }
    }

    override fun onItemSelected(position: Int, item: ContactEvent) {
        TODO("Not yet implemented")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}