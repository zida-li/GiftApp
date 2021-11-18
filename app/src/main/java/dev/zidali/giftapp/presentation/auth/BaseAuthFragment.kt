package dev.zidali.giftapp.presentation.auth

import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import dev.zidali.giftapp.business.datasource.datastore.AppDataStore
import dev.zidali.giftapp.presentation.UICommunicationListener
import dev.zidali.giftapp.util.Constants.Companion.TAG

@AndroidEntryPoint
abstract class BaseAuthFragment: Fragment() {

    lateinit var uiCommunicationListener: UICommunicationListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try{
            uiCommunicationListener = context as UICommunicationListener
        }catch(e: ClassCastException){
            Log.e(TAG, "$context must implement UICommunicationListener" )
        }

    }

}