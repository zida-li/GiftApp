package dev.zidali.giftapp.di

import android.app.Application
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.zidali.giftapp.business.datasource.cache.AppDatabase
import dev.zidali.giftapp.business.datasource.cache.AppDatabase.Companion.DATABASE_NAME
import dev.zidali.giftapp.business.datasource.datastore.AppDataStore
import dev.zidali.giftapp.business.datasource.datastore.AppDataStoreManager
import javax.inject.Singleton
import kotlin.coroutines.coroutineContext

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideAppDb(app: Application): AppDatabase {
        return Room
            .databaseBuilder(app, AppDatabase::class.java, DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideDataStoreManager(
        app: Application
    ): AppDataStore {
        return AppDataStoreManager(app)
    }

    @Singleton
    @Provides
    fun provideFireBaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Singleton
    @Provides
    fun provideFireBaseFireStore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

}