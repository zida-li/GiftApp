package dev.zidali.giftapp.presentation.main.contacts.contact_detail.gift

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dev.zidali.giftapp.R
import dev.zidali.giftapp.business.domain.models.Gift
import dev.zidali.giftapp.business.domain.util.*
import dev.zidali.giftapp.databinding.FragmentGiftBinding
import dev.zidali.giftapp.presentation.main.BaseMainFragment
import dev.zidali.giftapp.presentation.update.GlobalEvents
import dev.zidali.giftapp.util.Constants
import dev.zidali.giftapp.util.Constants.Companion.TAG
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
        globalManager.onTriggerEvent(GlobalEvents.SetGiftFragmentInView(true))
//        Log.d(Constants.TAG, "GiftFragment onResume()")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        subscribeObservers()
        initRecyclerView()
    }

    private fun subscribeObservers() {

        globalManager.state.observe(viewLifecycleOwner) { state ->

            if (state.needToUpdate) {
                viewModel.onTriggerEvent(GiftEvents.FetchContactPk)
                viewModel.onTriggerEvent(GiftEvents.FetchGifts)
                globalManager.onTriggerEvent(GlobalEvents.SetNeedToUpdate(false))
            }
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->

            uiCommunicationListener.displayProgressBar(state.isLoading)

            if (state.firstLoad) {
                viewModel.onTriggerEvent(GiftEvents.SetFirstLoad(false))
                globalManager.onTriggerEvent(GlobalEvents.SetNeedToUpdate(true))
            }

            recyclerAdapter?.apply {
                submitList(list = state.contact_gifts)
            }

            viewModel.toolbarState.observe(viewLifecycleOwner) {toolbarState->

                when(toolbarState) {

                    is GiftToolbarState.MultiSelectionState -> {
                        globalManager.onTriggerEvent(GlobalEvents.SetMultiSelection(true))
                        activity?.invalidateOptionsMenu()
                    }
                    is GiftToolbarState.RegularState -> {
                        viewModel.onTriggerEvent(GiftEvents.ClearSelectedGifts)
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
                        viewModel.onTriggerEvent(GiftEvents.OnRemoveHeadFromQueue)
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
                viewModel.onTriggerEvent(GiftEvents.SetToolBarState(GiftToolbarState.RegularState))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * RecyclerView
     */

    private fun initRecyclerView() {
        binding.giftRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@GiftFragment.context)
//            val topSpacingDecorator = TopSpacingItemDecoration(30)
//            removeItemDecoration(topSpacingDecorator)
//            addItemDecoration(topSpacingDecorator)
            recyclerAdapter = GiftListAdapter(
                this@GiftFragment,
                viewLifecycleOwner,
                viewModel.giftListInteractionManager.selectedGifts,
            )
            adapter = recyclerAdapter
        }
    }

    /**
     * Gift Functions
     */

    private fun confirmDeleteRequest() {
        val callback: AreYouSureCallback = object: AreYouSureCallback {

            override fun proceed() {
                viewModel.onTriggerEvent(GiftEvents.DeleteSelectedGifts)
                recyclerAdapter?.notifyDataSetChanged()
                viewModel.onTriggerEvent(GiftEvents.SetToolBarState(GiftToolbarState.RegularState))
            }

            override fun cancel() {
                //do nothing
            }
        }
        viewModel.onTriggerEvent(GiftEvents.AppendToMessageQueue(
            stateMessage = StateMessage(
                response = Response(
                    message = "Are You Sure? This cannot be undone",
                    uiComponentType = UIComponentType.AreYouSureDialog(callback),
                    messageType = MessageType.Info
                )
            )
        ))

    }

    override fun onItemSelected(position: Int, item: Gift) {
        if(isMultiSelectionModeEnabled()) {
            viewModel.onTriggerEvent(GiftEvents.AddOrRemoveGiftFromSelectedList(item))
        }
    }

    override fun activateMultiSelectionMode() {
        viewModel.onTriggerEvent(GiftEvents.SetToolBarState(GiftToolbarState.MultiSelectionState))
        viewModel.onTriggerEvent(GiftEvents.SetMultiSelectionMode(true))
    }

    override fun isMultiSelectionModeEnabled(): Boolean {
        return viewModel.giftListInteractionManager.isMultiSelectionStateActive()
    }

    override fun onIsCheckedClicked(item: Gift, position: Int) {
        viewModel.onTriggerEvent(GiftEvents.SetIsCheckedGift(item, position))
        if (item.isChecked) {
            recyclerAdapter?.notifyItemChanged(position)
            recyclerAdapter?.notifyItemMoved(position, viewModel.state.value?.contact_gifts?.size!!)
        } else {
            recyclerAdapter?.notifyItemChanged(position)
        }
    }

    /**
     * Others
     */

    override fun onPause() {
        super.onPause()
        globalManager.onTriggerEvent(GlobalEvents.SetGiftFragmentInView(false))
        viewModel.onTriggerEvent(GiftEvents.SetToolBarState(GiftToolbarState.RegularState))
        viewModel.onTriggerEvent(GiftEvents.SetMultiSelectionMode(false))
        uiCommunicationListener.hideSoftKeyboard()
//        Log.d(Constants.TAG, "GiftFragment onPause()")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recyclerAdapter = null
        _binding = null
        globalManager.onTriggerEvent(GlobalEvents.SetGiftFragmentInView(false))
//        Log.d(Constants.TAG, "GiftFragment onDestroyView()")
    }
}