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
            parentColumns = ["contact_name"],
            childColumns = ["contact_name"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)

data class GiftEntity (

    @PrimaryKey
    @ColumnInfo(name = "contact_gift")
    var contact_gift: String,

    @ColumnInfo(name = "contact_name")
    var contact_name: String,

)

fun Gift.toGiftEntity(): GiftEntity{
    return GiftEntity(
        contact_gift = contact_gift,
        contact_name = contact_name,
    )
}

fun GiftEntity.toGift(): Gift{
    return Gift(
        contact_gift = contact_gift,
        contact_name = contact_name,
    )
}