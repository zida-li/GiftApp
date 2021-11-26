package dev.zidali.giftapp.di.auth

import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.zidali.giftapp.business.datasource.cache.account.AccountPropertiesDao
import dev.zidali.giftapp.business.datasource.datastore.AppDataStore
import dev.zidali.giftapp.business.interactors.auth.LoginWithEmailAndPassword
import dev.zidali.giftapp.business.interactors.auth.LoginWithGoogle
import dev.zidali.giftapp.business.interactors.auth.RegisterWithEmailAndPassword
import dev.zidali.giftapp.business.interactors.auth.shared.GetEmail
import dev.zidali.giftapp.business.interactors.session.CheckPreviousAuthUser
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
        firebaseAuth: FirebaseAuth,
        accountPropertiesDao: AccountPropertiesDao,
    ): CheckPreviousAuthUser {
        return CheckPreviousAuthUser(
            firebaseAuth,
            accountPropertiesDao,
        )
    }

    @Singleton
    @Provides
    fun provideLoginWithGoogle(
        firebaseAuth: FirebaseAuth,
        accountPropertiesDao: AccountPropertiesDao
    ): LoginWithGoogle {
        return LoginWithGoogle(
            firebaseAuth,
            accountPropertiesDao
        )
    }

    @Singleton
    @Provides
    fun provideRegisterWithEmailAndPassword(
        firebaseAuth: FirebaseAuth,
        appDataStore: AppDataStore,
        accountPropertiesDao: AccountPropertiesDao,
    ): RegisterWithEmailAndPassword {
        return RegisterWithEmailAndPassword (
            firebaseAuth,
            appDataStore,
            accountPropertiesDao,
        )
    }

    @Singleton
    @Provides
    fun provideLoginWithEmailAndPassword(
        firebaseAuth: FirebaseAuth,
    ): LoginWithEmailAndPassword {
        return LoginWithEmailAndPassword(
            firebaseAuth
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

}