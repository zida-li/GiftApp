package dev.zidali.giftapp.presentation.main.all_events

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dev.zidali.giftapp.business.domain.models.ContactEvent

class AllEventListInteractionManager {

    private val _selectedEvents: MutableLiveData<ArrayList<ContactEvent>> = MutableLiveData()

    private val _toolBarState: MutableLiveData<AllEventToolbarState>
    = MutableLiveData(AllEventToolbarState.RegularState)

    val selectedEvents: LiveData<ArrayList<ContactEvent>>
        get() = _selectedEvents

    val toolbarState: LiveData<AllEventToolbarState>
        get() = _toolBarState

    fun setToolBarState(state: AllEventToolbarState) {
        _toolBarState.value = state
    }

    fun getSelectedEvents() : ArrayList<ContactEvent> = _selectedEvents.value?: ArrayList()

    fun isMultiSelectionStateActive(): Boolean {
        return _toolBarState.value.toString() == AllEventToolbarState.MultiSelectionState.toString()
    }

    fun addOrRemoveEventFromSelectedList(contactEvent: ContactEvent) {
        var list = _selectedEvents.value

        if(list == null) {
            list = ArrayList()
        }

        if (list.contains(contactEvent)) {
            list.remove(contactEvent)
        } else {
            list.add(contactEvent)
        }

        _selectedEvents.value = list
    }

    fun clearSelectedEvents() {
        _selectedEvents.value = null
    }

}