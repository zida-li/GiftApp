package dev.zidali.giftapp.business.datasource.cache.contacts

import androidx.room.*

@Dao
interface ContactDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contactEntity: ContactEntity)

    @Query("DELETE FROM contact")
    suspend fun clearContacts()

    @Query("SELECT * FROM contact WHERE contact_name = :contact_name")
    suspend fun getByName(contact_name: String): ContactEntity?

    @Query("SELECT * FROM contact")
    suspend fun getAllContacts(): MutableList<ContactEntity>

    @Query("UPDATE contact SET contact_name = :new_contact_name WHERE contact_name = :old_contact_name")
    suspend fun updateContact(new_contact_name: String, old_contact_name: Int)

}