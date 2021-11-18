package dev.zidali.giftapp.business.datasource.cache.auth

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AuthTokenDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(authTokenEntity: AuthTokenEntity): Long

    @Query("UPDATE auth_token SET token = null WHERE account_email = :email")
    suspend fun nullifyToken(email: String): Int

    @Query("DELETE FROM auth_token")
    suspend fun clearTokens()

    @Query("SELECT * FROM auth_token WHERE account_email = :email")
    suspend fun searchByEmail(email: String): AuthTokenEntity?

}