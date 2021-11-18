package dev.zidali.giftapp.business.datasource.cache.auth

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import dev.zidali.giftapp.business.datasource.cache.account.AccountPropertiesEntity
import dev.zidali.giftapp.business.domain.models.AuthToken

@Entity(
    tableName = "auth_token",
    foreignKeys = [
        ForeignKey(
            entity = AccountPropertiesEntity::class,
            parentColumns = ["email"],
            childColumns = ["account_email"],
            onDelete = CASCADE
        )
    ]
)
data class AuthTokenEntity(

    @PrimaryKey
    @ColumnInfo(name = "account_email")
    var email: String,

    @ColumnInfo(name = "token")
    var token: String? = null
)

fun AuthTokenEntity.toAuthToken(): AuthToken {
    if(token == null){
        throw Exception("Token cannot be null.")
    }
    return AuthToken(
        email = email,
        token = token!!,
    )
}

fun AuthToken.toEntity(): AuthTokenEntity{
    return AuthTokenEntity(
        email = email,
        token = token
    )
}

