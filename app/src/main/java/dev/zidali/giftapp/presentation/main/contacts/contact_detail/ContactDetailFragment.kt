package dev.zidali.giftapp.presentation.main.contacts.contact_detail

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayoutMediator
import dev.zidali.giftapp.R
import dev.zidali.giftapp.business.domain.util.StateMessageCallback
import dev.zidali.giftapp.databinding.FragmentContactDetailBinding
import dev.zidali.giftapp.presentation.main.BaseMainFragment
import dev.zidali.giftapp.presentation.main.MainActivity
import dev.zidali.giftapp.presentation.update.GlobalEvents
import dev.zidali.giftapp.util.processQueue


class ContactDetailFragment : BaseMainFragment() {

    private val viewModel: ContactDetailViewModel by viewModels()
    private var _binding: FragmentContactDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var menu: Menu
    private lateinit var searchView: SearchView

    private val titleArray = arrayOf(
        "Gifts",
        "Events"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentContactDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        initViewPager()
        subscribeObservers()
    }

    private fun subscribeObservers(){

        viewModel.state.observe(viewLifecycleOwner) { state ->

            (activity as MainActivity).supportActionBar?.title = state.contact_name

            if (state.isEditing) {
                activateEditMode()
            }

            if (!state.isEditing) {
                deactivateEditMode()
            }

            processQueue(
                context = context,
                queue = state.queue,
                stateMessageCallback = object : StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.onTriggerEvent(ContactDetailEvents.OnRemoveHeadFromQueue)
                    }
                }
            )
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        this.menu = menu
        if(isEditModeEnabled() && !globalManager.state.value?.multiSelectionActive!!) {
            inflater.inflate(R.menu.contact_menu_edit, this.menu)
        } else {
            if (!globalManager.state.value?.multiSelectionActive!!) {
                inflater.inflate(R.menu.contact_menu, this.menu)
                initSearchView()
            }
        }
    }

    private fun initSearchView() {
        activity?.apply {
            val searchManager: SearchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
            searchView = menu.findItem(R.id.action_edit).actionView as SearchView
            searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
            searchView.maxWidth = Integer.MAX_VALUE
            searchView.setIconifiedByDefault(true)
            searchView.isSubmitButtonEnabled = true
            searchView.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS

            val icon: ImageView = searchView.findViewById(androidx.appcompat.R.id.search_button)
            icon.setImageResource(R.drawable.ic_baseline_edit_24)

            val searchGoBtn: ImageView = searchView.findViewById(androidx.appcompat.R.id.search_go_btn)
            searchGoBtn.setImageResource(R.drawable.ic_baseline_check_24)
        }

        val searchPlate = searchView.findViewById(R.id.search_src_text) as EditText
        searchPlate.hint = ""

        viewModel.state.value?.let { state ->

            searchPlate.setText(state.contact_name)
            searchPlate.setSelection(searchPlate.length())

        }

        val searchCloseButton = searchView.findViewById(R.id.search_close_btn) as View
        searchCloseButton.setOnClickListener {
            searchView.onActionViewCollapsed()
            activity?.invalidateOptionsMenu()
        }

        val searchButton = searchView.findViewById(R.id.search_go_btn) as View
        searchButton.setOnClickListener{
            val searchQuery = searchPlate.text.toString()
            cacheState(searchQuery)
            globalManager.onTriggerEvent(GlobalEvents.SetNeedToUpdateContact(true))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId) {
            R.id.action_edit -> {
                viewModel.onTriggerEvent(ContactDetailEvents.ActivateEditMode)
            }
            R.id.action_finished -> {

            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun initViewPager() {

        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout

        val adapter = FragmentAdapter(
            childFragmentManager, lifecycle
        )

        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) {tab, position ->
            tab.text = titleArray[position]
        }.attach()

    }

    private fun activateEditMode() {

        activity?.invalidateOptionsMenu()
//        val editText = binding.contactName
//
//        editText.inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS
//        editText.requestFocus()
//        editText.setSelection(editText.length())
        uiCommunicationListener.showSoftKeyboard()
    }

    private fun deactivateEditMode() {
        activity?.invalidateOptionsMenu()
//        binding.contactName.inputType = InputType.TYPE_NULL
    }

    private fun cacheState(new_name: String) {
        viewModel.onTriggerEvent(ContactDetailEvents.OnUpdateContact(new_name))
        viewModel.onTriggerEvent(ContactDetailEvents.UpdateContact)
        viewModel.onTriggerEvent(ContactDetailEvents.UpdateTitle)
        viewModel.onTriggerEvent(ContactDetailEvents.DeactivateEditMode)
        uiCommunicationListener.hideSoftKeyboard()
    }

    private fun isEditModeEnabled(): Boolean {
        return viewModel.state.value?.isEditing!!
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}