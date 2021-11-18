package dev.zidali.giftapp.business.datasource.cache.auth

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(authTokenEntity: UserDataEntity): Long

    @Query("DELETE FROM userData")
    suspend fun clearTokens()

    @Query("SELECT * FROM userData WHERE email = :email")
    suspend fun searchByEmail(email: String): UserDataEntity?

}