package dev.zidali.giftapp.presentation.main.contacts.contact_detail.event

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dev.zidali.giftapp.business.domain.models.ContactEvent

class EventListInteractionManager {

    private val _selectedContactEvents: MutableLiveData<ArrayList<ContactEvent>> = MutableLiveData()

    private val _toolBarState: MutableLiveData<EventToolbarState>
    = MutableLiveData(EventToolbarState.RegularState)

    val selectedContactEvents: LiveData<ArrayList<ContactEvent>>
        get() = _selectedContactEvents

    val toolbarState: LiveData<EventToolbarState>
        get() = _toolBarState

    fun setToolBarState(state: EventToolbarState) {
        _toolBarState.value = state
    }

    fun getSelectedContactEvents() : ArrayList<ContactEvent> = _selectedContactEvents.value?: ArrayList()

    fun isMultiSelectionStateActive(): Boolean {
        return _toolBarState.value.toString() == EventToolbarState.MultiSelectionState.toString()
    }

    fun addOrRemoveContactEventFromSelectedList(contactEvent: ContactEvent) {
        var list = _selectedContactEvents.value

        if(list == null) {
            list = ArrayList()
        }

        if (list.contains(contactEvent)) {
            list.remove(contactEvent)
        } else {
            list.add(contactEvent)
        }

        _selectedContactEvents.value = list
    }

    fun clearSelectedContactEvents() {
        _selectedContactEvents.value = null
    }

}