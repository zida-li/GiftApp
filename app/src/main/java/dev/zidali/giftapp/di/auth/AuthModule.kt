package dev.zidali.giftapp.di.auth

import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.zidali.giftapp.business.datasource.cache.AppDatabase
import dev.zidali.giftapp.business.datasource.cache.account.AccountPropertiesDao
import dev.zidali.giftapp.business.datasource.cache.auth.AuthTokenDao
import dev.zidali.giftapp.business.datasource.datastore.AppDataStore
import dev.zidali.giftapp.business.interactors.auth.RegisterWithEmailAndPassword
import dev.zidali.giftapp.business.interactors.auth.shared.GetEmail
import dev.zidali.giftapp.business.interactors.session.CheckPreviousAuthUser
import dev.zidali.giftapp.business.interactors.session.Logout
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    /**
     *INTERACTORS
     */

    @Singleton
    @Provides
    fun provideCheckPreviousAuthUser(
        accountPropertiesDao: AccountPropertiesDao,
        authTokenDao: AuthTokenDao,
    ): CheckPreviousAuthUser {
        return CheckPreviousAuthUser(
            accountPropertiesDao,
            authTokenDao,
        )
    }

    @Singleton
    @Provides
    fun provideRegisterWithEmailAndPassword(
        firebaseAuth: FirebaseAuth,
        appDataStore: AppDataStore,
    ): RegisterWithEmailAndPassword {
        return RegisterWithEmailAndPassword (
            firebaseAuth,
            appDataStore,
        )
    }

    @Singleton
    @Provides
    fun provideLogout(
        authTokenDao: AuthTokenDao,
    ): Logout {
        return Logout(
            authTokenDao
        )
    }

    @Singleton
    @Provides
    fun provideGetEmail(
        appDataStore: AppDataStore
    ): GetEmail {
        return GetEmail(
            appDataStore
        )
    }

    /**
     * DATABASE
     */
    @Singleton
    @Provides
    fun provideAccountPropertiesDao(app: AppDatabase): AccountPropertiesDao {
        return app.getAccountPropertiesDao()
    }

    @Singleton
    @Provides
    fun provideAuthTokenDao(app:AppDatabase): AuthTokenDao {
        return app.getAuthTokenDao()
    }

}