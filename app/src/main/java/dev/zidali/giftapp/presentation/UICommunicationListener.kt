package dev.zidali.giftapp.presentation

interface UICommunicationListener {

    fun displayProgressBar(isLoading: Boolean)

    fun displayProgressBarForSessionManager(isLoading: Boolean)

    fun showSoftKeyboard()

    fun hideSoftKeyboard()

}