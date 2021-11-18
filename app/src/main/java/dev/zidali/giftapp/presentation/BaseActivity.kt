package dev.zidali.giftapp.presentation

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import dev.zidali.giftapp.presentation.session.SessionManager
import javax.inject.Inject

abstract class BaseActivity: AppCompatActivity(),
    UICommunicationListener {

    val TAG: String = "AppDebug"

    private var dialogInView: MaterialDialog? = null

    @Inject
    lateinit var sessionManager: SessionManager

    abstract override fun displayProgressBar(isLoading: Boolean)

    override fun hideSoftKeyboard() {
        if (currentFocus != null) {
            val inputMethodManager = getSystemService(
                Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager
                .hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
    }

    override fun onPause() {
        super.onPause()
        if(dialogInView != null){
            (dialogInView as MaterialDialog).dismiss()
            dialogInView = null
        }
    }
}