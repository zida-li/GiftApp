package dev.zidali.giftapp.di.main

import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.zidali.giftapp.business.datasource.cache.AppDatabase
import dev.zidali.giftapp.business.datasource.cache.contacts.ContactDao
import dev.zidali.giftapp.business.datasource.cache.contacts.ContactEventDao
import dev.zidali.giftapp.business.datasource.cache.contacts.GiftDao
import dev.zidali.giftapp.business.interactors.main.contacts.contact_detail.FetchEvents
import dev.zidali.giftapp.business.interactors.main.fab.CreateContact
import dev.zidali.giftapp.business.interactors.main.shared.FetchContacts
import dev.zidali.giftapp.business.interactors.main.contacts.contact_detail.FetchGifts
import dev.zidali.giftapp.business.interactors.main.fab.AddGift
import dev.zidali.giftapp.business.interactors.main.fab.CreateEvent
import dev.zidali.giftapp.business.interactors.main.shared.FetchAllEvents
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

    @Singleton
    @Provides
    fun provideFetchGifts(
        giftDao: GiftDao
    ): FetchGifts {
        return FetchGifts(
            giftDao
        )
    }

    @Singleton
    @Provides
    fun provideFetchEvents(
        contactEventDao: ContactEventDao
    ): FetchEvents {
        return FetchEvents(
            contactEventDao
        )
    }

    @Singleton
    @Provides
    fun provideFetchAllEvents(
        contactEventDao: ContactEventDao
    ): FetchAllEvents {
        return FetchAllEvents(
            contactEventDao
        )
    }

    @Singleton
    @Provides
    fun provideAddGift(
        giftDao: GiftDao,
        contactDao: ContactDao,
    ): AddGift {
        return AddGift(
            giftDao,
            contactDao,
        )
    }

    @Singleton
    @Provides
    fun provideCreateEvent(
        contactEventDao: ContactEventDao,
        contactDao: ContactDao,
    ): CreateEvent {
        return CreateEvent(
            contactEventDao,
            contactDao
        )
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
        return app.getContactEventDao()
    }

    @Singleton
    @Provides
    fun provideGiftDao(app: AppDatabase): GiftDao{
        return app.getGiftDao()
    }

}