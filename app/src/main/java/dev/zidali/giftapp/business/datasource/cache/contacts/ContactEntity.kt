package dev.zidali.giftapp.business.datasource.cache.contacts

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import dev.zidali.giftapp.business.datasource.cache.account.AccountPropertiesEntity
import dev.zidali.giftapp.business.domain.models.Contact

@Entity(
    tableName = "contact",
    foreignKeys = [
        ForeignKey(
            entity = AccountPropertiesEntity::class,
            parentColumns = ["current_authUser_email"],
            childColumns = ["contact_name"],
            onDelete = CASCADE
        )
    ]
)
data class ContactEntity(

    @PrimaryKey
    @ColumnInfo(name ="contact_name")
    var contact_name: String,

    @ColumnInfo(name = "current_authUser_email")
    var current_authUser_email: String,

)

fun ContactEntity.toContact(): Contact {
    return Contact(
        contact_name = contact_name,
        current_authUser_email = current_authUser_email,
    )
}

fun Contact.toContactsEntity(): ContactEntity{
    return ContactEntity(
        contact_name = contact_name!!,
        current_authUser_email = current_authUser_email!!,
    )
}

