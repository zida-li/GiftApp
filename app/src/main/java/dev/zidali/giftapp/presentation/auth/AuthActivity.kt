package dev.zidali.giftapp.presentation.auth

import android.os.Bundle
import android.view.View
import dagger.hilt.android.AndroidEntryPoint
import dev.zidali.giftapp.R
import dev.zidali.giftapp.business.domain.util.StateMessageCallback
import dev.zidali.giftapp.databinding.ActivityAuthBinding
import dev.zidali.giftapp.presentation.BaseActivity
import dev.zidali.giftapp.presentation.session.SessionEvents
import dev.zidali.giftapp.util.processQueue

@AndroidEntryPoint
class AuthActivity : BaseActivity() {

    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        subscribeObservers()
    }

    private fun subscribeObservers() {
        sessionManager.state.observe(this) {state->

            displayProgressBar(state.isLoading)

            processQueue(
                context = this,
                queue = state.queue,
                stateMessageCallback = object : StateMessageCallback {
                    override fun removeMessageFromStack() {
                        sessionManager.onTriggerEvent(SessionEvents.OnRemoveHeadFromQueue)
                    }
                }
            )

            if(state.didCheckForPreviousAuthUser) {
                onFinishCheckPreviousAuthUser()
            }

            if(state.authToken != null) {

            }
        }
    }

    private fun onFinishCheckPreviousAuthUser() {
        binding.fragmentContainer.visibility = View.VISIBLE
        binding.splashLogo.visibility = View.INVISIBLE
    }

    override fun displayProgressBar(isLoading: Boolean) {
        //nothing
    }
}