package dev.zidali.giftapp.business.datasource.cache.contacts

import androidx.room.*

@Dao
interface ContactDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contactEntity: ContactEntity): Long

    @Query("DELETE FROM contact")
    suspend fun clearContacts()

    @Delete
    suspend fun deleteContacts(contact: ContactEntity)

    @Query("SELECT * FROM contact WHERE primary_key = :primary_key")
    suspend fun getByPk(primary_key: Int): ContactEntity?

    @Query("SELECT * FROM contact")
    suspend fun getAllContacts(): MutableList<ContactEntity>

    @Query("UPDATE contact SET contact_name = :new_contact_name WHERE primary_key = :primary_key")
    suspend fun updateContact(new_contact_name: String, primary_key: Int)

}