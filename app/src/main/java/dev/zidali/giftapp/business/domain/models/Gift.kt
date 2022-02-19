package dev.zidali.giftapp.business.domain.models

data class Gift (
    var contact_gift: String,
    var contact_name: String,
    var pk: Int,
    var isChecked: Boolean = false,
    var isMultiSelectionModeEnabled: Boolean = false,
    var gift_pk: Int,
)