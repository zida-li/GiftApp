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
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.zidali.giftapp.R
import dev.zidali.giftapp.business.datasource.cache.AppDatabase
import dev.zidali.giftapp.business.datasource.cache.AppDatabase.Companion.DATABASE_NAME
import dev.zidali.giftapp.business.datasource.cache.AppDatabase.Companion.MIGRATION_1_2
import dev.zidali.giftapp.business.datasource.cache.account.AccountPropertiesDao
import dev.zidali.giftapp.business.datasource.datastore.AppDataStore
import dev.zidali.giftapp.business.datasource.datastore.AppDataStoreManager
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

}