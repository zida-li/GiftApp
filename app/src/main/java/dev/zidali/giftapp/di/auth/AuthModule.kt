package dev.zidali.giftapp.di.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dev.zidali.giftapp.business.datasource.cache.account.AccountPropertiesDao
import dev.zidali.giftapp.business.datasource.datastore.AppDataStore
import dev.zidali.giftapp.business.interactors.auth.LoginWithEmailAndPassword
import dev.zidali.giftapp.business.interactors.auth.LoginWithGoogle
import dev.zidali.giftapp.business.interactors.auth.RegisterWithEmailAndPassword
import dev.zidali.giftapp.business.interactors.auth.shared.GetEmail

@Module
@InstallIn(ActivityRetainedComponent::class)
object AuthModule {

    /**
     *INTERACTORS
     */

    @ActivityRetainedScoped
    @Provides
    fun provideLoginWithGoogle(
        firebaseAuth: FirebaseAuth,
        accountPropertiesDao: AccountPropertiesDao,
        fireStore: FirebaseFirestore,
        appDataStore: AppDataStore,
    ): LoginWithGoogle {
        return LoginWithGoogle(
            firebaseAuth,
            accountPropertiesDao,
            fireStore,
            appDataStore,
        )
    }

    @ActivityRetainedScoped
    @Provides
    fun provideRegisterWithEmailAndPassword(
        firebaseAuth: FirebaseAuth,
        appDataStore: AppDataStore,
        accountPropertiesDao: AccountPropertiesDao,
        fireStore: FirebaseFirestore,
    ): RegisterWithEmailAndPassword {
        return RegisterWithEmailAndPassword (
            firebaseAuth,
            appDataStore,
            accountPropertiesDao,
            fireStore,
        )
    }

    @ActivityRetainedScoped
    @Provides
    fun provideLoginWithEmailAndPassword(
        firebaseAuth: FirebaseAuth,
        accountPropertiesDao: AccountPropertiesDao,
        fireStore: FirebaseFirestore,
        appDataStore: AppDataStore,
    ): LoginWithEmailAndPassword {
        return LoginWithEmailAndPassword(
            firebaseAuth,
            accountPropertiesDao,
            fireStore,
            appDataStore,
        )
    }

    @ActivityRetainedScoped
    @Provides
    fun provideGetEmail(
        appDataStore: AppDataStore
    ): GetEmail {
        return GetEmail(
            appDataStore
        )
    }

}