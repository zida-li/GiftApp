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

    @Query("SELECT * FROM gift WHERE gift_pk = :gift_pk")
    suspend fun searchByGift(gift_pk: Int): GiftEntity?

    @Query("SELECT ALL * FROM gift WHERE primary_key = :primary_key")
    suspend fun getAllGiftByContact(primary_key: Int): MutableList<GiftEntity>

    @Query("SELECT * FROM gift")
    suspend fun getAllContacts(): MutableList<GiftEntity>

    @Query("UPDATE gift SET contact_name = :new_contact_name WHERE primary_key = :primary_key")
    suspend fun updateContactNameGift(new_contact_name: String, primary_key: Int)

    @Query("UPDATE gift SET isChecked = :isChecked WHERE gift_pk = :gift_pk")
    suspend fun updateIsChecked(isChecked: Boolean, gift_pk: Int,)

}