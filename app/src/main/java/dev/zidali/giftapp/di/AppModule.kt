package dev.zidali.giftapp.di

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.room.Room
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.zidali.giftapp.business.datasource.cache.AppDatabase
import dev.zidali.giftapp.business.datasource.cache.AppDatabase.Companion.DATABASE_NAME
import dev.zidali.giftapp.business.datasource.cache.AppDatabase.Companion.MIGRATION_1_2
import dev.zidali.giftapp.business.datasource.cache.account.AccountPropertiesDao
import dev.zidali.giftapp.business.datasource.cache.contacts.ContactDao
import dev.zidali.giftapp.business.datasource.cache.contacts.ContactEventDao
import dev.zidali.giftapp.business.datasource.cache.contacts.GiftDao
import dev.zidali.giftapp.business.datasource.datastore.AppDataStore
import dev.zidali.giftapp.business.datasource.datastore.AppDataStoreManager
import dev.zidali.giftapp.business.interactors.session.CheckPreviousAuthUser
import dev.zidali.giftapp.business.interactors.session.DeleteAccount
import dev.zidali.giftapp.business.interactors.session.Logout
import dev.zidali.giftapp.util.Constants.Companion.default_web_client_id
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Singleton
    @Provides
    fun provideDataStoreManager(
        app: Application
    ): AppDataStore {
        return AppDataStoreManager(app)
    }

    @Singleton
    @Provides
    fun provideConnectivityManager(
        @ApplicationContext context: Context
    ) = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    @Singleton
    @Provides
    fun provideNetworkRequest(): NetworkRequest.Builder{
        return NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
    }

    /**
     * FIREBASE
     */

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

    @Singleton
    @Provides
    fun provideFirebaseFirestoreSettings(): FirebaseFirestoreSettings {
        return FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
            .build()
    }

    @Singleton
    @Provides
    fun provideGoogleSignInClient(
        @ApplicationContext applicationContext: Context
    ): GoogleSignInClient {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken((default_web_client_id))
            .build()
        return GoogleSignIn.getClient(applicationContext, googleSignInOptions)
    }

    /**
     * DATABASE
     */

    @Singleton
    @Provides
    fun provideAppDb(app: Application): AppDatabase {
        return Room
            .databaseBuilder(app, AppDatabase::class.java, DATABASE_NAME)
            .addMigrations(MIGRATION_1_2)
            .build()
    }

    @Singleton
    @Provides
    fun provideAccountPropertiesDao(app: AppDatabase): AccountPropertiesDao {
        return app.getAccountPropertiesDao()
    }

    @Singleton
    @Provides
    fun provideContactsDao(app: AppDatabase): ContactDao {
        return app.getContactDao()
    }

    @Singleton
    @Provides
    fun provideContactEventDao(app: AppDatabase): ContactEventDao {
        return app.getContactEventDao()
    }

    @Singleton
    @Provides
    fun provideGiftDao(app: AppDatabase): GiftDao {
        return app.getGiftDao()
    }

    /**
     * INTERACTORS
     */

    @Singleton
    @Provides
    fun provideDeleteAccount(
        accountPropertiesDao: AccountPropertiesDao,
        firebaseAuth: FirebaseAuth,
        fireStore: FirebaseFirestore,
        connectivityManager: ConnectivityManager,
    ): DeleteAccount {
        return DeleteAccount(
            accountPropertiesDao,
            firebaseAuth,
            fireStore,
            connectivityManager
        )
    }

    @Singleton
    @Provides
    fun provideLogout(
        firebaseAuth: FirebaseAuth,
        googleSignInClient: GoogleSignInClient,
    ): Logout {
        return Logout(
            firebaseAuth,
            googleSignInClient,
        )
    }

    @Singleton
    @Provides
    fun provideCheckPreviousAuthUser(
        firebaseAuth: FirebaseAuth,
        accountPropertiesDao: AccountPropertiesDao,
    ): CheckPreviousAuthUser {
        return CheckPreviousAuthUser(
            firebaseAuth,
            accountPropertiesDao,
        )
    }

}