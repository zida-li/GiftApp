package dev.zidali.giftapp.presentation.main.contacts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dev.zidali.giftapp.business.domain.models.Contact

class ContactListInteractionManager {

    private val _selectedContacts: MutableLiveData<ArrayList<Contact>> = MutableLiveData()

    private val _toolBarState: MutableLiveData<ContactToolbarState>
    = MutableLiveData(ContactToolbarState.RegularState)

    val selectedContacts: LiveData<ArrayList<Contact>>
        get() = _selectedContacts

    val toolbarState: LiveData<ContactToolbarState>
        get() = _toolBarState

    fun setToolBarState(state: ContactToolbarState) {
        _toolBarState.value = state
    }

    fun getSelectedContacts() : ArrayList<Contact> = _selectedContacts.value?: ArrayList()

    fun isMultiSelectionStateActive(): Boolean {
        return _toolBarState.value.toString() == ContactToolbarState.MultiSelectionState.toString()
    }

    fun addOrRemoveContactFromSelectedList(contact: Contact) {
        var list = _selectedContacts.value

        if(list == null) {
            list = ArrayList()
        }

        if (list.contains(contact)) {
            list.remove(contact)
        } else {
            list.add(contact)
        }

        _selectedContacts.value = list
    }

    fun clearSelectedContacts() {
        _selectedContacts.value = null
    }

}