package dev.zidali.giftapp.business.datasource.cache.contacts

import androidx.room.*

@Dao
interface ContactEventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contactEventEntity: ContactEventEntity): Long

    @Delete
    suspend fun deleteEvent(event: ContactEventEntity)

    @Query("DELETE FROM contact_events")
    suspend fun clearContactEvents()

    @Query("SELECT * FROM contact_events WHERE contact_name = :contact_name AND contact_event = :contact_event")
    suspend fun searchByEvent(contact_name: String, contact_event: String): ContactEventEntity?

    @Query("SELECT * FROM contact_events WHERE contact_name = :contact_name")
    suspend fun getAllEventsOfContact (contact_name: String): MutableList<ContactEventEntity>

    @Query("SELECT * FROM contact_events")
    suspend fun getAllContactEvents(): MutableList<ContactEventEntity>

    @Query("UPDATE contact_events SET contact_name = :new_contact_name WHERE primary_key = :primary_key")
    suspend fun updateContactNameEvent(new_contact_name: String, primary_key: Int)

    @Query("UPDATE contact_events SET contact_event_reminder = :new_contact_reminder WHERE primary_key = :primary_key AND contact_event = :contact_event")
    suspend fun updateContactReminder(new_contact_reminder: String, primary_key: Int, contact_event: String)

    @Query("UPDATE contact_events SET contact_event = :new_contact_event, contact_event_reminder = :new_contact_reminder, year = :year, month = :month, day = :day WHERE contact_event = :old_event AND contact_name = :contact_name")
    suspend fun updateContactEvent(new_contact_event: String, new_contact_reminder: String, year: Int, month: Int, day: Int, old_event: String, contact_name: String)
}