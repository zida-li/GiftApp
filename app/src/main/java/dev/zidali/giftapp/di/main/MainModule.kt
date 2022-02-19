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
import dev.zidali.giftapp.business.interactors.main.contacts.DeleteContacts
import dev.zidali.giftapp.business.interactors.main.contacts.contact_detail.DeleteGifts
import dev.zidali.giftapp.business.interactors.main.contacts.contact_detail.FetchEvents
import dev.zidali.giftapp.business.interactors.main.fab.CreateContact
import dev.zidali.giftapp.business.interactors.main.contacts.contact_detail.FetchGifts
import dev.zidali.giftapp.business.interactors.main.fab.AddGift
import dev.zidali.giftapp.business.interactors.main.fab.CreateEvent
import dev.zidali.giftapp.business.interactors.main.shared.*
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
        )
    }

    @Singleton
    @Provides
    fun provideUpdateContact(
        contactDao: ContactDao,
        giftDao: GiftDao,
        contactEventDao: ContactEventDao,
    ): UpdateContact {
        return UpdateContact (
            contactDao,
            giftDao,
            contactEventDao,
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

    @Singleton
    @Provides
    fun provideDeleteContacts(
        contactDao: ContactDao
    ): DeleteContacts {
        return DeleteContacts(
            contactDao
        )
    }

    @Singleton
    @Provides
    fun provideDeleteEvents(
        contactEventDao: ContactEventDao
    ): DeleteEvents {
        return DeleteEvents(
            contactEventDao
        )
    }

    @Singleton
    @Provides
    fun provideDeleteGifts(
        giftDao: GiftDao
    ): DeleteGifts {
        return DeleteGifts(
            giftDao
        )
    }

    @Singleton
    @Provides
    fun provideUpdateContactEventReminder(
        contactEventDao: ContactEventDao
    ): UpdateContactEventReminder {
        return UpdateContactEventReminder(
            contactEventDao
        )
    }

    @Singleton
    @Provides
    fun provideFetchEvent(
        contactEventDao: ContactEventDao
    ): FetchEvent {
        return FetchEvent (
            contactEventDao
        )
    }

    @Singleton
    @Provides
    fun provideUpdateEvent(
        contactEventDao: ContactEventDao
    ): UpdateEvent {
        return UpdateEvent (
            contactEventDao
        )
    }

    @Singleton
    @Provides
    fun provideUpdateGift(
        giftDao: GiftDao
    ): SetIsCheckedGift {
        return SetIsCheckedGift(
            giftDao
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