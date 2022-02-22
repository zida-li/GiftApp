package dev.zidali.giftapp.util

import com.google.firebase.crashlytics.FirebaseCrashlytics
import dev.zidali.giftapp.util.Constants.Companion.DEBUG

fun cLog(msg: String?){
    msg?.let {
        if(!DEBUG){
            FirebaseCrashlytics.getInstance().log(it)
        }
    }

}