package dev.zidali.giftapp.business.datasource.cache.account

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.zidali.giftapp.business.domain.models.AccountProperties

@Entity(tableName = "account_properties")
data class AccountPropertiesEntity (

    @PrimaryKey
    @ColumnInfo(name = "current_authUser_email")
    var current_authUser_email: String,

    )

fun AccountPropertiesEntity.toAccountProperties(): AccountProperties {
    return AccountProperties(
        current_authUser_email = current_authUser_email,
    )
}

fun AccountProperties.toEntity(): AccountPropertiesEntity{
    return AccountPropertiesEntity(
        current_authUser_email = current_authUser_email,
    )
}