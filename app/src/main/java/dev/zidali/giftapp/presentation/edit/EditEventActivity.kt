package dev.zidali.giftapp.presentation.edit

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.core.view.isInvisible
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.zidali.giftapp.R
import dev.zidali.giftapp.business.domain.util.StateMessageCallback
import dev.zidali.giftapp.databinding.ActivityEditEventBinding
import dev.zidali.giftapp.presentation.BaseActivity
import dev.zidali.giftapp.presentation.session.SessionEvents
import dev.zidali.giftapp.util.processQueue

@AndroidEntryPoint
class EditEventActivity: BaseActivity() {

    private lateinit var binding: ActivityEditEventBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()
        subscribeObservers()

    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbar)
        navController = findNavController(R.id.nav_host_fragment_content_edit_event)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.eventsFragment, R.id.contactFragment,
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    private fun subscribeObservers() {

        globalManager.state.observe(this) {state->

             if(state.editFragmentInView) {
                 supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_close_24)
                 supportActionBar?.setDisplayHomeAsUpEnabled(true)
             }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                if(navController.currentDestination?.displayName!! == "dev.zidali.giftapp:id/eventDetailFragment") {
                    finish()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun displayProgressBar(isLoading: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_edit_event)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}