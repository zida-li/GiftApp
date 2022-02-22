package dev.zidali.giftapp.di.main

import android.net.ConnectivityManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
        contactDao: ContactDao,
        firebaseAuth: FirebaseAuth,
        fireStore: FirebaseFirestore,
    ): CreateContact {
        return CreateContact(
            contactDao,
            firebaseAuth,
            fireStore,
        )
    }

    @Singleton
    @Provides
    fun provideFetchContacts(
        contactDao: ContactDao,
        firebaseAuth: FirebaseAuth,
        fireStore: FirebaseFirestore,
        connectivityManager: ConnectivityManager,
    ): FetchContacts {
        return FetchContacts(
            contactDao,
            firebaseAuth,
            fireStore,
            connectivityManager
        )
    }

    @Singleton
    @Provides
    fun provideFetchGifts(
        giftDao: GiftDao,
        firebaseAuth: FirebaseAuth,
        fireStore: FirebaseFirestore,
    ): FetchGifts {
        return FetchGifts(
            giftDao,
            firebaseAuth,
            fireStore,
        )
    }

    @Singleton
    @Provides
    fun provideFetchEvents(
        contactEventDao: ContactEventDao,
        firebaseAuth: FirebaseAuth,
        fireStore: FirebaseFirestore,
    ): FetchEvents {
        return FetchEvents(
            contactEventDao,
            firebaseAuth,
            fireStore,
        )
    }

    @Singleton
    @Provides
    fun provideFetchAllEvents(
        contactEventDao: ContactEventDao,
        firebaseAuth: FirebaseAuth,
        fireStore: FirebaseFirestore,
    ): FetchAllEvents {
        return FetchAllEvents(
            contactEventDao,
            firebaseAuth,
            fireStore,
        )
    }

    @Singleton
    @Provides
    fun provideAddGift(
        giftDao: GiftDao,
        firebaseAuth: FirebaseAuth,
        fireStore: FirebaseFirestore,
    ): AddGift {
        return AddGift(
            giftDao,
            firebaseAuth,
            fireStore,
        )
    }

    @Singleton
    @Provides
    fun provideCreateEvent(
        contactEventDao: ContactEventDao,
        firebaseAuth: FirebaseAuth,
        fireStore: FirebaseFirestore,
    ): CreateEvent {
        return CreateEvent(
            contactEventDao,
            firebaseAuth,
            fireStore,
        )
    }

    @Singleton
    @Provides
    fun provideUpdateContact(
        contactDao: ContactDao,
        giftDao: GiftDao,
        contactEventDao: ContactEventDao,
        firebaseAuth: FirebaseAuth,
        fireStore: FirebaseFirestore,
    ): UpdateContact {
        return UpdateContact (
            contactDao,
            giftDao,
            contactEventDao,
            firebaseAuth,
            fireStore,
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
        contactDao: ContactDao,
        firebaseAuth: FirebaseAuth,
        fireStore: FirebaseFirestore,
    ): DeleteContacts {
        return DeleteContacts(
            contactDao,
            firebaseAuth,
            fireStore,
        )
    }

    @Singleton
    @Provides
    fun provideDeleteEvents(
        contactEventDao: ContactEventDao,
        firebaseAuth: FirebaseAuth,
        fireStore: FirebaseFirestore,
    ): DeleteEvents {
        return DeleteEvents(
            contactEventDao,
            firebaseAuth,
            fireStore,
        )
    }

    @Singleton
    @Provides
    fun provideDeleteGifts(
        giftDao: GiftDao,
        firebaseAuth: FirebaseAuth,
        fireStore: FirebaseFirestore,
    ): DeleteGifts {
        return DeleteGifts(
            giftDao,
            firebaseAuth,
            fireStore,
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
        giftDao: GiftDao,
        firebaseAuth: FirebaseAuth,
        fireStore: FirebaseFirestore,
    ): SetIsCheckedGift {
        return SetIsCheckedGift(
            giftDao,
            firebaseAuth,
            fireStore,
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