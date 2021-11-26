package dev.zidali.giftapp.business.datasource.cache.contacts

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface GiftDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(giftEntity: GiftEntity): Long

    @Query("DELETE FROM gift")
    suspend fun clearContacts()

    @Query("SELECT * FROM gift WHERE contact_gift = :contact_gift")
    suspend fun searchByGift(contact_gift: String): GiftEntity?

    @Query("SELECT ALL * FROM gift WHERE contact_name = :contact_name")
    suspend fun getAllGiftByContact(contact_name: String): MutableList<GiftEntity>

    @Query("SELECT * FROM gift")
    suspend fun getAllContacts(): MutableList<GiftEntity>

}