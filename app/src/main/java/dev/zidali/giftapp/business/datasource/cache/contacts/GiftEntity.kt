package dev.zidali.giftapp.business.datasource.cache.contacts

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import dev.zidali.giftapp.business.domain.models.Gift

@Entity(
    tableName = "gift",
    foreignKeys = [
        ForeignKey(
            entity = ContactEntity::class,
            parentColumns = ["primary_key"],
            childColumns = ["primary_key"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)

data class GiftEntity (

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "gift_pk")
    var gift_pk: Int,

    @ColumnInfo(name = "contact_gift")
    var contact_gift: String,

    @ColumnInfo(name = "contact_name")
    var contact_name: String,

    @ColumnInfo(name = "primary_key")
    var contact_pk: Int,

    @ColumnInfo(name ="isChecked")
    var isChecked: Boolean,

    @ColumnInfo(name = "isMultiSelectionModeEnabled")
    var isMultiSelectionModeEnabled: Boolean,

    @ColumnInfo(name = "gift_owner")
    var gift_owner: String,

    ) {

    constructor(): this(
        0,
        "",
        "",
        0,
        false,
        false,
        "",
    )

}

fun Gift.toGiftEntity(): GiftEntity{
    return GiftEntity(
        contact_gift = contact_gift,
        contact_name = contact_name,
        contact_pk = contact_pk,
        isChecked = isChecked,
        isMultiSelectionModeEnabled = isMultiSelectionModeEnabled,
        gift_pk = gift_pk,
        gift_owner = gift_owner,
    )
}

fun GiftEntity.toGift(): Gift{
    return Gift(
        contact_gift = contact_gift,
        contact_name = contact_name,
        contact_pk = contact_pk,
        isChecked = isChecked,
        isMultiSelectionModeEnabled = isMultiSelectionModeEnabled,
        gift_pk = gift_pk,
        gift_owner = gift_owner,
    )
}