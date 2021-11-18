package dev.zidali.giftapp.business.datasource.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.zidali.giftapp.business.datasource.cache.account.AccountPropertiesDao
import dev.zidali.giftapp.business.datasource.cache.account.AccountPropertiesEntity
import dev.zidali.giftapp.business.datasource.cache.auth.UserDataDao
import dev.zidali.giftapp.business.datasource.cache.auth.UserDataEntity

@Database(entities = [
    AccountPropertiesEntity::class,
    UserDataEntity::class
], version = 1)
abstract class AppDatabase: RoomDatabase() {

    abstract fun getAccountPropertiesDao(): AccountPropertiesDao

    abstract fun getAuthTokenDao(): UserDataDao

    companion object {
        const val DATABASE_NAME = "app_db"
    }

}