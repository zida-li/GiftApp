package dev.zidali.giftapp.presentation.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dev.zidali.giftapp.R
import dev.zidali.giftapp.business.domain.util.*
import dev.zidali.giftapp.databinding.ActivityMainBinding
import dev.zidali.giftapp.presentation.BaseActivity
import dev.zidali.giftapp.presentation.auth.AuthActivity
import dev.zidali.giftapp.presentation.main.fab.add_gift.AddGiftFragment
import dev.zidali.giftapp.presentation.main.fab.create_contact.CreateContactFragment
import dev.zidali.giftapp.presentation.main.fab.create_event.CreateEventFragment
import dev.zidali.giftapp.presentation.session.SessionEvents
import dev.zidali.giftapp.util.processQueue

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    //Animations
    private val rotateOpen: Animation by lazy { AnimationUtils.loadAnimation(applicationContext, R.anim.rotate_open_anim) }
    private val rotateClose: Animation by lazy { AnimationUtils.loadAnimation(applicationContext, R.anim.rotate_close_anim) }
    private val fromBottom: Animation by lazy { AnimationUtils.loadAnimation(applicationContext, R.anim.from_bottom_anim) }
    private val toBottom: Animation by lazy { AnimationUtils.loadAnimation(applicationContext, R.anim.to_bottom_anim) }
    private var clicked = false

    //Navigation & DrawerLayout
    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initOnClickListeners()
        subscribeObservers()
        setupActionBar()
        setupAppBar()

//        NotificationHelper.createNotification(
//            this,
//            "Test",
//            "whatdoesthisdo",
//            "",
//            false
//        )
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.appBarMain.toolbar)
        navController = findNavController(R.id.nav_host_fragment_content_main)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment, R.id.contactFragment,
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    private fun setupAppBar() {
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setupWithNavController(navController)
    }

    private fun subscribeObservers() {

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
     * OnClick Listeners
     */

    private fun initOnClickListeners() {
        binding.fabMenu.setOnClickListener {
            onMenuButtonClicked()
        }

        binding.fabAddEvent.setOnClickListener {

            val dialog = CreateEventFragment()
            val supportFragmentManager = supportFragmentManager

            dialog.isCancelable = false
            dialog.show(supportFragmentManager, "createContactDialog")
        }

        binding.fabAddGift.setOnClickListener{
            val dialog = AddGiftFragment()
            val supportFragmentManager = supportFragmentManager

            dialog.isCancelable = false
            dialog.show(supportFragmentManager, "addGiftDialog")
        }

        binding.fabAddContact.setOnClickListener {
            val createContactFragment = CreateContactFragment()
            val supportFragmentManager = supportFragmentManager

            supportFragmentManager.setFragmentResultListener(
                "ADD_CONTACT_RESULT",
                this
            ) {resultKey, bundle ->
                if(resultKey == "ADD_CONTACT_RESULT") {
                    val contact = bundle.getString("ADDED_CONTACT")
                    Snackbar.make(binding.mainActivity, contact!!, Snackbar.LENGTH_SHORT)
                        .setAction(R.string.view_contacts) {
                            //action navigate to contacts.
                        }.show()
                }
            }
            createContactFragment.isCancelable = false
            createContactFragment.show(supportFragmentManager, "CreateContactFragment")
        }
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
            binding.fabAddContact.visibility = View.VISIBLE
            binding.fabAddGift.visibility = View.VISIBLE
            binding.fabAddEvent.visibility = View.VISIBLE
        } else {
            binding.fabAddContact.visibility = View.INVISIBLE
            binding.fabAddGift.visibility = View.INVISIBLE
            binding.fabAddEvent.visibility = View.INVISIBLE
        }
    }

    private fun setAnimation(clicked:Boolean) {
        if(!clicked) {
            binding.fabAddContact.startAnimation(fromBottom)
            binding.fabAddGift.startAnimation(fromBottom)
            binding.fabAddEvent.startAnimation(fromBottom)
            binding.fabMenu.startAnimation(rotateOpen)
        } else {
            binding.fabAddContact.startAnimation(toBottom)
            binding.fabAddGift.startAnimation(toBottom)
            binding.fabAddEvent.startAnimation(toBottom)
            binding.fabMenu.startAnimation(rotateClose)
        }
    }

    private fun setClickable(clicked: Boolean) {
        if(!clicked) {
            binding.fabAddContact.isClickable = true
            binding.fabAddGift.isClickable = true
            binding.fabAddEvent.isClickable = true
        } else {
            binding.fabAddContact.isClickable = false
            binding.fabAddGift.isClickable = false
            binding.fabAddEvent.isClickable = false
        }
    }

}