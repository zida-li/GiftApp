package dev.zidali.giftapp.business.datasource.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dev.zidali.giftapp.business.datasource.cache.account.AccountPropertiesDao
import dev.zidali.giftapp.business.datasource.cache.account.AccountPropertiesEntity
import dev.zidali.giftapp.business.datasource.cache.contacts.*

@Database(entities = [
    AccountPropertiesEntity::class,
    ContactEntity::class,
    ContactEventEntity::class,
    GiftEntity::class,
], version = 2)
abstract class AppDatabase: RoomDatabase() {

    abstract fun getAccountPropertiesDao(): AccountPropertiesDao

    abstract fun getContactDao(): ContactDao

    abstract fun getContactEventDao(): ContactEventDao

    abstract fun getGiftDao(): GiftDao

    companion object {
        const val DATABASE_NAME = "app_db"

        val MIGRATION_1_2 = object: Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                //did not alter table, currently nothing.
            }
        }
    }


}