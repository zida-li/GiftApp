package dev.zidali.giftapp.presentation.main

import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import dev.zidali.giftapp.presentation.UICommunicationListener
import dev.zidali.giftapp.presentation.update.UpdateManager
import dev.zidali.giftapp.util.Constants
import javax.inject.Inject

@AndroidEntryPoint
abstract class BaseMainFragment : Fragment() {

    @Inject
    lateinit var updateManager: UpdateManager

    lateinit var uiCommunicationListener: UICommunicationListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try{
            uiCommunicationListener = context as UICommunicationListener
        }catch(e: ClassCastException){
            Log.e(Constants.TAG, "$context must implement UICommunicationListener" )
        }

    }

}