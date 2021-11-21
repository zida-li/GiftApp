package dev.zidali.giftapp.presentation.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import dev.zidali.giftapp.R
import dev.zidali.giftapp.business.domain.util.StateMessageCallback
import dev.zidali.giftapp.databinding.ActivityMainBinding
import dev.zidali.giftapp.presentation.BaseActivity
import dev.zidali.giftapp.presentation.auth.AuthActivity
import dev.zidali.giftapp.presentation.main.create_contact.CreateContactFragment
import dev.zidali.giftapp.presentation.session.SessionEvents
import dev.zidali.giftapp.util.processQueue

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    //Animations
    private val rotateOpen: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim) }
    private val rotateClose: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim) }
    private val fromBottom: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim) }
    private val toBottom: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim) }
    private var clicked = false

    //Navigation & DrawerLayout
    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navigationView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        subscribeObservers()

        initNavDrawer()

        binding.appBarMain.fabMenu.setOnClickListener {

            onMenuButtonClicked()

//            val dialog = CreateContactFragment()
//
//            dialog.show(supportFragmentManager, "createContactDialog")
        }
    }

    private fun initNavDrawer() {
        setSupportActionBar(binding.appBarMain.toolbar)
        drawerLayout = binding.drawerLayout
        navigationView = binding.navView
        navController = findNavController(R.id.nav_host_fragment_content_main)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment, R.id.contactsFragment,
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navigationView.setupWithNavController(navController)
    }

    private fun subscribeObservers() {

        navigationView = binding.navView

        val header = navigationView.getHeaderView(0)

        sessionManager.state.observe(this) { state ->

            displayProgressBar(state.isLoading)

            processQueue(
                context = this,
                queue = state.queue,
                stateMessageCallback = object : StateMessageCallback {
                    override fun removeMessageFromStack() {
                        sessionManager.onTriggerEvent(SessionEvents.OnRemoveHeadFromQueue)
                    }
                })

            if (state.accountProperties == null) {
                navAuthActivity()
            }

            header.findViewById<TextView>(R.id.text_description).text = state.accountProperties?.email

        }
    }

    private fun navAuthActivity() {
        val intent = Intent(this, AuthActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    override fun displayProgressBar(isLoading: Boolean) {
        //
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    /**
     * FAB Menu Functions & Animations
     */
    private fun onMenuButtonClicked() {
        setVisibility(clicked)
        setAnimation(clicked)
        setClickable(clicked)
        clicked = !clicked
    }

    private fun setVisibility(clicked: Boolean) {
        if(!clicked) {
            binding.appBarMain.fabAddContact.visibility = View.VISIBLE
            binding.appBarMain.fabAddGift.visibility = View.VISIBLE
            binding.appBarMain.fabAddEvent.visibility = View.VISIBLE
        } else {
            binding.appBarMain.fabAddContact.visibility = View.INVISIBLE
            binding.appBarMain.fabAddGift.visibility = View.INVISIBLE
            binding.appBarMain.fabAddEvent.visibility = View.INVISIBLE
        }
    }

    private fun setAnimation(clicked:Boolean) {
        if(!clicked) {
            binding.appBarMain.fabAddContact.startAnimation(fromBottom)
            binding.appBarMain.fabAddGift.startAnimation(fromBottom)
            binding.appBarMain.fabAddEvent.startAnimation(fromBottom)
            binding.appBarMain.fabMenu.startAnimation(rotateOpen)
        } else {
            binding.appBarMain.fabAddContact.startAnimation(toBottom)
            binding.appBarMain.fabAddGift.startAnimation(toBottom)
            binding.appBarMain.fabAddEvent.startAnimation(toBottom)
            binding.appBarMain.fabMenu.startAnimation(rotateClose)
        }
    }

    private fun setClickable(clicked: Boolean) {
        if(!clicked) {
            binding.appBarMain.fabAddContact.isClickable = true
            binding.appBarMain.fabAddGift.isClickable = true
            binding.appBarMain.fabAddEvent.isClickable = true
        } else {
            binding.appBarMain.fabAddContact.isClickable = false
            binding.appBarMain.fabAddGift.isClickable = false
            binding.appBarMain.fabAddEvent.isClickable = false
        }
    }
}