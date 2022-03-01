package dev.zidali.giftapp.business.datasource.cache.account

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AccountPropertiesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAndReplace(accountPropertiesEntity: AccountPropertiesEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnore(accountPropertiesEntity: AccountPropertiesEntity?): Long

    @Query("SELECT * FROM account_properties WHERE current_authUser_email = :email")
    suspend fun searchByEmail(email: String): AccountPropertiesEntity?

    @Query("SELECT * FROM account_properties")
    suspend fun getAllUsers(): MutableList<AccountPropertiesEntity>

    @Query("DELETE FROM account_properties WHERE current_authUser_email = :email")
    suspend fun deleteUser(email: String)

}