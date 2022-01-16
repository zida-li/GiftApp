package dev.zidali.giftapp.presentation.main.contacts.contact_detail.gift

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dev.zidali.giftapp.business.domain.models.Gift
import dev.zidali.giftapp.business.domain.util.StateMessageCallback
import dev.zidali.giftapp.databinding.FragmentGiftBinding
import dev.zidali.giftapp.presentation.main.BaseMainFragment
import dev.zidali.giftapp.presentation.main.MainActivity
import dev.zidali.giftapp.util.TopSpacingItemDecoration
import dev.zidali.giftapp.util.processQueue

class GiftFragment : BaseMainFragment(),
GiftListAdapter.Interaction
{

    private var recyclerAdapter: GiftListAdapter? = null
    private val viewModel: GiftViewModel by viewModels()
    private var _binding: FragmentGiftBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGiftBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.onTriggerEvent(GiftEvents.FetchGifts)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.onTriggerEvent(GiftEvents.FetchContactName)
        //removing contact name from appbar because it's already in the title box.
//        (activity as MainActivity).supportActionBar?.title = viewModel.state.value?.contact_name
        subscribeObservers()
        initRecyclerView()
    }

    private fun subscribeObservers() {
        viewModel.state.observe(viewLifecycleOwner, {state->

            recyclerAdapter?.apply {
                submitList(list = state.contact_gifts)
            }

            processQueue(
                context = context,
                queue = state.queue,
                stateMessageCallback = object: StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.onTriggerEvent(GiftEvents.OnRemoveHeadFromQueue)
                    }
                }
            )

        })
    }

    private fun initRecyclerView() {
        binding.giftRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@GiftFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(30)
            removeItemDecoration(topSpacingDecorator)
            addItemDecoration(topSpacingDecorator)
            recyclerAdapter = GiftListAdapter(this@GiftFragment)
            adapter = recyclerAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemSelected(position: Int, item: Gift) {
        TODO("Not yet implemented")
    }
}