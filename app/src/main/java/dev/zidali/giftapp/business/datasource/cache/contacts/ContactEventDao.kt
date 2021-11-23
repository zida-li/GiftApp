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

    @Query("SELECT * FROM contact_events")
    suspend fun getAllContactEvents(): MutableList<ContactEventEntity>

}