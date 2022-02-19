package dev.zidali.giftapp.presentation.main.contacts.contact_detail.gift

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.zidali.giftapp.business.datasource.datastore.AppDataStore
import dev.zidali.giftapp.business.domain.models.Gift
import dev.zidali.giftapp.business.domain.util.*
import dev.zidali.giftapp.business.interactors.main.contacts.contact_detail.DeleteGifts
import dev.zidali.giftapp.business.interactors.main.contacts.contact_detail.FetchGifts
import dev.zidali.giftapp.business.interactors.main.shared.SetIsCheckedGift
import dev.zidali.giftapp.presentation.util.DataStoreKeys
import dev.zidali.giftapp.util.Constants
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class GiftViewModel
@Inject
constructor(
    private val appDataStore: AppDataStore,
    private val fetchGifts: FetchGifts,
    private val deleteGifts: DeleteGifts,
    private val setIsCheckedGift: SetIsCheckedGift,
): ViewModel() {

    val state: MutableLiveData<GiftState> = MutableLiveData(GiftState())

    val giftListInteractionManager = GiftListInteractionManager()

    val toolbarState: LiveData<GiftToolbarState>
        get() = giftListInteractionManager.toolbarState

    fun onTriggerEvent(event: GiftEvents) {

        when(event) {
            is GiftEvents.FetchGifts -> {
                fetchGifts()
            }
            is GiftEvents.FetchContactPk -> {
                fetchContactPk()
            }
            is GiftEvents.SetFirstLoad -> {
                setFirstLoad(event.boolean)
            }
            is GiftEvents.SetToolBarState -> {
                setToolBarState(event.state)
            }
            is GiftEvents.AddOrRemoveGiftFromSelectedList -> {
                addOrRemoveGiftFromSelectedList(event.gift)
            }
            is GiftEvents.ClearSelectedGifts -> {
                clearSelectedGifts()
            }
            is GiftEvents.DeleteSelectedGifts -> {
                deleteSelectedGifts()
            }
            is GiftEvents.AppendToMessageQueue -> {
                appendToMessageQueue(event.stateMessage)
            }
            is GiftEvents.OnRemoveHeadFromQueue -> {
                onRemoveHeadFromQueue()
            }
            is GiftEvents.SetMultiSelectionMode -> {
                setMultiSelectionMode(event.boolean)
            }
            is GiftEvents.SetIsCheckedGift -> {
                setIsChecked(event.gift)
            }
        }
    }

    private fun fetchGifts() {
        state.value?.let { state->
            fetchGifts.execute(
                state.contact_pk.toInt()
            ).onEach { dataState ->

                dataState.data?.let { gift->
                    this.state.value = state.copy(contact_gifts = gift.contact_gifts)
                }

            }.launchIn(viewModelScope)
        }
    }

    private fun fetchContactPk() {
        state.value?.let {state->
            flow<GiftState> {
                val contactPk = appDataStore.readValue(DataStoreKeys.SELECTED_CONTACT_PK)
                emit(GiftState(
                    contact_pk = contactPk?.toInt()!!
                ))
            }.onEach {
                this.state.value = state.copy(contact_pk = it.contact_pk)
            }.launchIn(viewModelScope)
        }
    }

    private fun setFirstLoad(boolean: Boolean) {
        state.value?.let {state->
            this.state.value = state.copy(
                firstLoad = boolean
            )
        }
    }

    /**
     * MultiSelectionMode
     */

    private fun setToolBarState(state: GiftToolbarState) {
        giftListInteractionManager.setToolBarState(state)
    }

    private fun addOrRemoveGiftFromSelectedList(gift: Gift) {
        giftListInteractionManager.addOrRemoveGiftFromSelectedList(gift)
    }

    private fun clearSelectedGifts() {
        giftListInteractionManager.clearSelectedGifts()
    }

    private fun deleteSelectedGifts() {
        if(getSelectedGifts().size > 0) {
            deleteGifts.execute(getSelectedGifts()).launchIn(viewModelScope)
            removeSelectedContactsFromList()
        }
    }

    /**
     * Alert Dialogs
     */

    private fun appendToMessageQueue(stateMessage: StateMessage){
        state.value?.let { state ->
            val queue = state.queue
            if(!stateMessage.doesMessageAlreadyExistInQueue(queue = queue)){
                if(stateMessage.response.uiComponentType !is UIComponentType.None){
                    queue.add(stateMessage)
                    this.state.value = state.copy(queue = queue)
                }
            }
        }
    }

    private fun onRemoveHeadFromQueue() {
        state.value?.let { state ->
            try {
                val queue = state.queue
                queue.remove() // can throw exception if empty
                this.state.value = state.copy(queue = queue)
            } catch (e: Exception) {
                Log.d(Constants.TAG, "removeHeadFromQueue: Nothing to remove from DialogQueue")
            }
        }
    }

    /**
     * Supporting Functions
     */

    private fun getSelectedGifts(): ArrayList<Gift> {
        return giftListInteractionManager.getSelectedGifts()
    }

    private fun removeSelectedContactsFromList() {
        state.value?.contact_gifts?.removeAll(getSelectedGifts())
        clearSelectedGifts()
    }

    private fun setMultiSelectionMode(boolean: Boolean) {
        state.value?.let { state->
            for(gift in state.contact_gifts) {
                gift.isMultiSelectionModeEnabled = boolean
            }
        }
    }

    private fun setIsChecked(item: Gift) {
        state.value?.let { state->
            for(gift in state.contact_gifts) {
                if(gift.contact_gift == item.contact_gift) {
                    item.isChecked = !item.isChecked
                    setIsCheckedGift.execute(item).launchIn(viewModelScope)
                }
            }
            if(item.isChecked) {
                state.contact_gifts.remove(item)
                state.contact_gifts.add(item)
            }
        }
    }

}