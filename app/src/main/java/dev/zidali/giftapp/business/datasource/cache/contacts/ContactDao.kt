package dev.zidali.giftapp.business.datasource.cache.contacts

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ContactDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contactEntity: ContactEntity): Long

    @Query("DELETE FROM contact")
    suspend fun clearContacts()

    @Query("SELECT * FROM contact WHERE contact_name = :contact_name")
    suspend fun searchByName(contact_name: String): ContactEntity?

    @Query("SELECT * FROM contact")
    suspend fun getAllContacts(): MutableList<ContactEntity>

}