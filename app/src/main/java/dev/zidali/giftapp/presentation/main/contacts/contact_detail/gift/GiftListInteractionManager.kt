package dev.zidali.giftapp.presentation.main.contacts.contact_detail.gift

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dev.zidali.giftapp.business.domain.models.Gift

class GiftListInteractionManager {

    private val _selectedGifts: MutableLiveData<ArrayList<Gift>> = MutableLiveData()

    private val _toolBarState: MutableLiveData<GiftToolbarState>
    = MutableLiveData(GiftToolbarState.RegularState)

    val selectedGifts: LiveData<ArrayList<Gift>>
        get() = _selectedGifts

    val toolbarState: LiveData<GiftToolbarState>
        get() = _toolBarState

    fun setToolBarState(state: GiftToolbarState) {
        _toolBarState.value = state
    }

    fun getSelectedGifts() : ArrayList<Gift> = _selectedGifts.value?: ArrayList()

    fun isMultiSelectionStateActive(): Boolean {
        return _toolBarState.value.toString() == GiftToolbarState.MultiSelectionState.toString()
    }

    fun addOrRemoveGiftFromSelectedList(gift: Gift) {
        var list = _selectedGifts.value

        if(list == null) {
            list = ArrayList()
        }

        if (list.contains(gift)) {
            list.remove(gift)
        } else {
            list.add(gift)
        }

        _selectedGifts.value = list
    }

    fun clearSelectedGifts() {
        _selectedGifts.value = null
    }

}