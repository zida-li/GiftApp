package dev.zidali.giftapp.business.datasource.cache.contacts

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ContactEventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contactEventEntity: ContactEventEntity): Long

    @Query("DELETE FROM contact_events")
    suspend fun clearContactEvents()

    @Query("SELECT * FROM contact_events WHERE contact_event = :contact_event")
    suspend fun searchByEvent(contact_event: String): ContactEventEntity?

    @Query("SELECT * FROM contact_events WHERE contact_name = :contact_name")
    suspend fun getAllEventsOfContact (contact_name: String): MutableList<ContactEventEntity>

    @Query("SELECT * FROM contact_events")
    suspend fun getAllContactEvents(): MutableList<ContactEventEntity>

    @Query("UPDATE contact_events SET contact_name = :new_contact_name WHERE primary_key = :primary_key")
    suspend fun updateContactNameEvent(new_contact_name: String, primary_key: Int)
}