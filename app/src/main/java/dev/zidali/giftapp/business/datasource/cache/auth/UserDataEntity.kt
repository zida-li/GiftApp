package dev.zidali.giftapp.business.datasource.cache.auth

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import dev.zidali.giftapp.business.datasource.cache.account.AccountPropertiesEntity
import dev.zidali.giftapp.business.domain.models.AuthToken

@Entity(
    tableName = "userData",
    foreignKeys = [
        ForeignKey(
            entity = AccountPropertiesEntity::class,
            parentColumns = ["email"],
            childColumns = ["email"],
            onDelete = CASCADE
        )
    ]
)
data class UserDataEntity(

    @PrimaryKey
    @ColumnInfo(name = "email")
    var email: String,

)

fun UserDataEntity.toAuthToken(): AuthToken {
    return AuthToken(
        email = email,
    )
}

fun AuthToken.toEntity(): UserDataEntity{
    return UserDataEntity(
        email = email,
    )
}

