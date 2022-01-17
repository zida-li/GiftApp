package dev.zidali.giftapp.presentation.update

sealed class GlobalEvents {

    object GlobalComplete: GlobalEvents()

    object RequestGlobal: GlobalEvents()

    object GiftFragmentInView: GlobalEvents()

    object GiftFragmentOutOfView: GlobalEvents()

    object EventFragmentInView: GlobalEvents()

    object EventFragmentOutOfView: GlobalEvents()

}
