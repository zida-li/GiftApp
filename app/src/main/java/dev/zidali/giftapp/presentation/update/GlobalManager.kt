package dev.zidali.giftapp.presentation.update

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
            is GlobalEvents.SetNeedToUpdateEventFragment -> {
                setNeedToUpdateEventFragment(event.boolean)
            }
            is GlobalEvents.GiftFragmentInView -> {
                setGiftFragmentView(event.boolean)
            }
            is GlobalEvents.EventFragmentInView -> {
                setEventFragmentView(event.boolean)
            }
        }
    }

    private fun setNeedToUpdate(boolean: Boolean) {
        state.value?.let { state->
            this.state.value = state.copy(needToUpdate = boolean)
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

}