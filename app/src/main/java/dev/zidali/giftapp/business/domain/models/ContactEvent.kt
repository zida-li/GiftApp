package dev.zidali.giftapp.business.domain.models

data class ContactEvent(

    var contact_name: String,
    var contact_event: String,
    var contact_event_reminder: String,
    var year: Int,
    var month: Int,
    var day: Int,

)