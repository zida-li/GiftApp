package dev.zidali.giftapp.business.datasource.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.zidali.giftapp.business.datasource.cache.account.AccountPropertiesDao
import dev.zidali.giftapp.business.datasource.cache.account.AccountPropertiesEntity
import dev.zidali.giftapp.business.datasource.cache.auth.AuthTokenDao
import dev.zidali.giftapp.business.datasource.cache.auth.AuthTokenEntity

@Database(entities = [
    AccountPropertiesEntity::class,
    AuthTokenEntity::class
], version = 1)
abstract class AppDatabase: RoomDatabase() {

    abstract fun getAccountPropertiesDao(): AccountPropertiesDao

    abstract fun getAuthTokenDao(): AuthTokenDao

    companion object {
        const val DATABASE_NAME = "app_db"
    }

}