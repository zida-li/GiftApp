package dev.zidali.giftapp.business.datasource.cache.contacts

import androidx.room.*

@Dao
interface GiftDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(giftEntity: GiftEntity): Long

    @Query("DELETE FROM gift")
    suspend fun clearContacts()

    @Delete
    suspend fun deleteGift(gift: GiftEntity)

    @Query("SELECT * FROM gift WHERE contact_gift = :contact_gift")
    suspend fun searchByGift(contact_gift: String): GiftEntity?

    @Query("SELECT ALL * FROM gift WHERE contact_name = :contact_name")
    suspend fun getAllGiftByContact(contact_name: String): MutableList<GiftEntity>

    @Query("SELECT * FROM gift")
    suspend fun getAllContacts(): MutableList<GiftEntity>

    @Query("UPDATE gift SET contact_name = :new_contact_name WHERE primary_key = :primary_key")
    suspend fun updateContactNameGift(new_contact_name: String, primary_key: Int)

    @Query("UPDATE gift SET isChecked = :isChecked WHERE contact_name =:contact_name AND contact_gift =:contact_gift")
    suspend fun updateIsChecked(isChecked: Boolean, contact_name: String, contact_gift: String)

}