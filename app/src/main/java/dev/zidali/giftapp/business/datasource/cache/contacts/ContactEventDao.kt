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

    @Query("SELECT * FROM contact_events WHERE primary_key =:primary_key AND event_pk = :event_pk")
    suspend fun searchByEvent(primary_key: Int, event_pk: Int): ContactEventEntity?

    @Query("SELECT * FROM contact_events WHERE primary_key =:primary_key ORDER BY expired ASC, ymd_format ASC")
    suspend fun getAllEventsOfContact (primary_key: Int): MutableList<ContactEventEntity>

    @Query("SELECT * FROM contact_events WHERE event_owner = :event_owner ORDER BY expired ASC, ymd_format ASC")
    suspend fun getAllOwnerEvents(event_owner: String): MutableList<ContactEventEntity>

    @Query("UPDATE contact_events SET contact_name = :new_contact_name WHERE primary_key = :primary_key")
    suspend fun updateContactNameEvent(new_contact_name: String, primary_key: Int)

    @Query("UPDATE contact_events SET contact_event_reminder = :new_contact_reminder WHERE primary_key = :primary_key AND event_pk = :event_pk")
    suspend fun updateContactReminder(new_contact_reminder: String, primary_key: Int, event_pk: Int,)

    @Query("UPDATE contact_events SET contact_event = :new_contact_event, contact_event_reminder = :new_contact_reminder, year = :year, month = :month, day = :day, ymd_format = :ymd_format, expired = :expired WHERE event_pk = :event_pk")
    suspend fun updateContactEvent(new_contact_event: String, new_contact_reminder: String, year: Int, month: Int, day: Int, ymd_format: String, expired: Boolean, event_pk: Int)
}