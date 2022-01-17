package dev.zidali.giftapp.presentation.update

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateManager
@Inject
constructor(
) {

    private val TAG: String = "AppDebug"

    val state: MutableLiveData<UpdateState> = MutableLiveData(UpdateState())

    fun onTriggerEvent(event: UpdateEvents){
        when(event){
            UpdateEvents.UpdateComplete -> {
                resetSwitch()
            }
            UpdateEvents.RequestUpdate -> {
                activateSwitch()
            }
        }
    }

    private fun resetSwitch(){
        state.value?.let { state->
            this.state.value = state.copy(needToUpdateContactPage = false)
        }
    }

    private fun activateSwitch() {
        state.value?.let { state->
            this.state.value = state.copy(needToUpdateContactPage = true)
        }
    }

}