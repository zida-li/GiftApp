package dev.zidali.giftapp.presentation.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.isInvisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dev.zidali.giftapp.R
import dev.zidali.giftapp.business.domain.util.*
import dev.zidali.giftapp.databinding.ActivityMainBinding
import dev.zidali.giftapp.presentation.BaseActivity
import dev.zidali.giftapp.presentation.auth.AuthActivity
import dev.zidali.giftapp.presentation.main.contacts.contact_detail.gift.GiftEvents
import dev.zidali.giftapp.presentation.main.fab.add_gift.AddGiftFragment
import dev.zidali.giftapp.presentation.main.fab.create_contact.CreateContactFragment
import dev.zidali.giftapp.presentation.main.fab.create_event.CreateEventFragment
import dev.zidali.giftapp.presentation.session.SessionEvents
import dev.zidali.giftapp.util.processQueue
import java.time.LocalDate
import java.util.*

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    //Navigation & DrawerLayout
    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initOnClickListeners()
        subscribeObservers()
        setupActionBar()
        setupAppBar()
        initNavDrawer()

        if(navController.currentDestination?.displayName!! == "dev.zidali.giftapp:id/contactFragment") {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24)
        }

    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbar)
        navController = findNavController(R.id.nav_host_fragment_content_main)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.eventDetailFragment
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    private fun setupAppBar() {
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setupWithNavController(navController)
        bottomNavigationView.setOnItemReselectedListener { item->

            when(item.itemId) {
                R.id.nav_contact -> {
                    navController.popBackStack(R.id.contactFragment, false)
                }
            }

        }

    }

    private fun initNavDrawer() {
        drawerLayout = binding.mainActivity
        navigationView = binding.navView
        binding.toolbar.setNavigationOnClickListener {
            if((navController.currentDestination?.displayName!! == "dev.zidali.giftapp:id/contactFragment" ||
                        navController.currentDestination?.displayName!! == "dev.zidali.giftapp:id/eventsFragment")) {
                binding.mainActivity.open()
            } else {
                navController.popBackStack()
            }
        }

        navigationView.setNavigationItemSelectedListener { menuItem ->

            when(menuItem.itemId) {

                R.id.log_out -> {
                    sessionManager.onTriggerEvent(SessionEvents.Logout)
                }
                R.id.delete_account -> {
                    confirmDeleteRequest()
                }
            }
            true
        }
    }

    private fun subscribeObservers() {

        navigationView = binding.navView

        val header = navigationView.getHeaderView(0)

        sessionManager.state.observe(this) { state ->
            
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

            header.findViewById<TextView>(R.id.text_description).text = state.accountProperties?.current_authUser_email

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
        if(isLoading){
//            Log.d(TAG, "mainActivity: ${isLoading.toString()}")
            binding.progressBar.visibility = View.VISIBLE
        } else {
//            Log.d(TAG, "mainActivity: ${isLoading.toString()}")
            binding.progressBar.visibility = View.GONE
        }
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

            if(navController.currentDestination?.displayName!! == "dev.zidali.giftapp:id/contactFragment") {
                val createContactFragment = CreateContactFragment()
                val supportFragmentManager = supportFragmentManager

                supportFragmentManager.setFragmentResultListener(
                    "ADD_CONTACT_RESULT",
                    this
                ) {resultKey, bundle ->
                    if(resultKey == "ADD_CONTACT_RESULT") {
                        val contact = bundle.getString("ADDED_CONTACT")
                        Snackbar.make(binding.mainActivity, contact!! + " Added to Contacts", Snackbar.LENGTH_SHORT).show()
                    }
                }
                createContactFragment.isCancelable = false
                createContactFragment.show(supportFragmentManager, "CreateContactFragment")
            }

            if(navController.currentDestination?.displayName!! == "dev.zidali.giftapp:id/contactDetailFragment"
                && globalManager.state.value?.giftFragmentInView!!) {
                val dialog = AddGiftFragment()
                val supportFragmentManager = supportFragmentManager

                dialog.isCancelable = false
                dialog.show(supportFragmentManager, "addGiftDialog")
            } else if (navController.currentDestination?.displayName!! == "dev.zidali.giftapp:id/contactDetailFragment"
                && globalManager.state.value?.eventFragmentInView!!) {

                val dialog = CreateEventFragment()
                val supportFragmentManager = supportFragmentManager

                dialog.isCancelable = false
                dialog.show(supportFragmentManager, "createContactDialog")
            }

            if(navController.currentDestination?.displayName!! == "dev.zidali.giftapp:id/eventsFragment") {
                val dialog = CreateEventFragment()
                val supportFragmentManager = supportFragmentManager

                dialog.isCancelable = false
                dialog.show(supportFragmentManager, "createContactDialog")
            }

//            Log.d(TAG, navController.currentDestination?.displayName!!)
        }

    }

    private fun confirmDeleteRequest() {
        val callback: AreYouSureCallback = object: AreYouSureCallback {

            override fun proceed() {
                sessionManager.onTriggerEvent(SessionEvents.DeleteAccount)
            }

            override fun cancel() {
                //do nothing
            }
        }
        sessionManager.onTriggerEvent(SessionEvents.AppendToMessageQueue(
            stateMessage = StateMessage(
                response = Response(
                    message = "Are You Sure? This cannot be undone",
                    uiComponentType = UIComponentType.AreYouSureDialog(callback),
                    messageType = MessageType.Info
                )
            )
        ))

    }


}