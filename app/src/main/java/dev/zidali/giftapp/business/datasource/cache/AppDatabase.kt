package dev.zidali.giftapp.business.datasource.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.zidali.giftapp.business.datasource.cache.account.AccountPropertiesDao
import dev.zidali.giftapp.business.datasource.cache.account.AccountPropertiesEntity
import dev.zidali.giftapp.business.datasource.cache.contacts.ContactDao
import dev.zidali.giftapp.business.datasource.cache.contacts.ContactEventDao
import dev.zidali.giftapp.business.datasource.cache.contacts.ContactEntity
import dev.zidali.giftapp.business.datasource.cache.contacts.ContactEventEntity
import dev.zidali.giftapp.business.domain.models.ContactEvent

@Database(entities = [
    AccountPropertiesEntity::class,
    ContactEntity::class,
    ContactEventEntity::class
], version = 2)
abstract class AppDatabase: RoomDatabase() {

    abstract fun getAccountPropertiesDao(): AccountPropertiesDao

    abstract fun getContactDao(): ContactDao

    abstract fun getContactEvent(): ContactEventDao

    companion object {
        const val DATABASE_NAME = "app_db"
    }

}