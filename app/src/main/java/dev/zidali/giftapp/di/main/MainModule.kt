package dev.zidali.giftapp.di.main

import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.zidali.giftapp.business.datasource.cache.AppDatabase
import dev.zidali.giftapp.business.datasource.cache.account.AccountPropertiesDao
import dev.zidali.giftapp.business.datasource.cache.contacts.ContactDao
import dev.zidali.giftapp.business.datasource.cache.contacts.ContactEventDao
import dev.zidali.giftapp.business.interactors.main.CreateContact
import dev.zidali.giftapp.business.interactors.main.contacts.FetchContacts
import dev.zidali.giftapp.business.interactors.session.Logout
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MainModule {

    /**
     * INTERACTORS
     */

    @Singleton
    @Provides
    fun provideCreateContact(
        contactDao: ContactDao
    ): CreateContact {
        return CreateContact(
            contactDao
        )
    }

    @Singleton
    @Provides
    fun provideFetchContacts(
        contactDao: ContactDao
    ): FetchContacts {
        return FetchContacts(
            contactDao
        )
    }

    /**
     * DATABASE
     */

    @Singleton
    @Provides
    fun provideContactsDao(app: AppDatabase): ContactDao {
        return app.getContactDao()
    }

    @Singleton
    @Provides
    fun provideContactEventDao(app: AppDatabase): ContactEventDao {
        return app.getContactEvent()
    }

    @Singleton
    @Provides
    fun provideLogout(
        firebaseAuth: FirebaseAuth,
    ): Logout {
        return Logout(
            firebaseAuth
        )
    }

}