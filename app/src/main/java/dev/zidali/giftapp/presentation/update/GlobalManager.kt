package dev.zidali.giftapp.presentation.update

import android.util.Log
import androidx.lifecycle.MutableLiveData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GlobalManager
@Inject
constructor(
) {

    private val TAG: String = "AppDebug"

    val state: MutableLiveData<GlobalState> = MutableLiveData(GlobalState())

    fun onTriggerEvent(event: GlobalEvents){
        when(event){
            is GlobalEvents.SetNeedToUpdate -> {
                setNeedToUpdate(event.boolean)
            }
            is GlobalEvents.SetNeedToUpdateContact -> {
                setNeedToUpdateContact(event.boolean)
            }
            is GlobalEvents.SetNeedToUpdateEventFragment -> {
                setNeedToUpdateEventFragment(event.boolean)
            }
            is GlobalEvents.SetGiftFragmentInView -> {
                setGiftFragmentView(event.boolean)
            }
            is GlobalEvents.SetEventFragmentInView -> {
                setEventFragmentView(event.boolean)
            }
            is GlobalEvents.SetEventDetailFragmentView -> {
                setEventDetailFragmentView(event.boolean)
            }
            is GlobalEvents.SetMultiSelection -> {
                setMultiSelection(event.boolean)
            }
        }
    }

    private fun setNeedToUpdate(boolean: Boolean) {
        state.value?.let { state->
            this.state.value = state.copy(needToUpdate = boolean)
        }
    }

    private fun setNeedToUpdateContact(boolean: Boolean) {
        state.value?.let { state->
            this.state.value = state.copy(needToUpdateContact = boolean)
        }
    }

    private fun setNeedToUpdateEventFragment(boolean: Boolean) {
        state.value?.let { state->
            this.state.value = state.copy(
                needToUpdateEventFragment = boolean
            )
        }
    }

    private fun setGiftFragmentView(boolean: Boolean) {
        state.value?.let { state->
            this.state.value = state.copy(giftFragmentInView = boolean)
        }
    }

    private fun setEventFragmentView(boolean: Boolean) {
        state.value?.let { state->
            this.state.value = state.copy(eventFragmentInView = boolean)
        }
    }

    private fun setEventDetailFragmentView(boolean: Boolean) {
        state.value?.let {state->
            this.state.value = state.copy(editFragmentInView = boolean)
        }
    }

    private fun setMultiSelection(boolean: Boolean) {
        state.value?.let { state->
            this.state.value = state.copy(multiSelectionActive = boolean)
        }
    }

}