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
import dev.zidali.giftapp.presentation.update.GlobalEvents
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
        globalManager.onTriggerEvent(GlobalEvents.GiftFragmentInView(true))
//        Log.d(Constants.TAG, "GiftFragment onResume()")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //removing contact name from appbar because it's already in the title box.
//        (activity as MainActivity).supportActionBar?.title = viewModel.state.value?.contact_name
        subscribeObservers()
        initRecyclerView()
    }

    private fun subscribeObservers() {

        globalManager.state.observe(viewLifecycleOwner, { state->

            if(state.needToUpdate){
                viewModel.onTriggerEvent(GiftEvents.FetchContactName)
                viewModel.onTriggerEvent(GiftEvents.FetchGifts)
                globalManager.onTriggerEvent(GlobalEvents.SetNeedToUpdate(false))
            }
        })

        viewModel.state.observe(viewLifecycleOwner, {state->

            if(state.firstLoad) {
                viewModel.onTriggerEvent(GiftEvents.SetFirstLoad(false))
                globalManager.onTriggerEvent(GlobalEvents.SetNeedToUpdate(true))
            }

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

    override fun onPause() {
        super.onPause()
        globalManager.onTriggerEvent(GlobalEvents.GiftFragmentInView(false))
//        Log.d(Constants.TAG, "GiftFragment onPause()")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        globalManager.onTriggerEvent(GlobalEvents.GiftFragmentInView(false))
//        Log.d(Constants.TAG, "GiftFragment onDestroyView()")
    }

    override fun onItemSelected(position: Int, item: Gift) {
        TODO("Not yet implemented")
    }
}