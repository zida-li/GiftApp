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
            GlobalEvents.GlobalComplete -> {
                setNeedToUpdateContactPage(false)
            }
            GlobalEvents.RequestGlobal -> {
                setNeedToUpdateContactPage(true)
            }
            GlobalEvents.GiftFragmentInView -> {
                setGiftFragmentView(true)
            }
            GlobalEvents.GiftFragmentOutOfView -> {
                setGiftFragmentView(false)
            }
            GlobalEvents.EventFragmentInView -> {
                setEventFragmentView(true)
            }
            GlobalEvents.EventFragmentOutOfView -> {
                setEventFragmentView(false)
            }
        }
    }

    private fun setNeedToUpdateContactPage(boolean: Boolean) {
        state.value?.let { state->
            this.state.value = state.copy(needToUpdateContactPage = boolean)
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