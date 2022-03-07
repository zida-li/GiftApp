package dev.zidali.giftapp.di.main

import android.net.ConnectivityManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dev.zidali.giftapp.business.datasource.cache.contacts.ContactDao
import dev.zidali.giftapp.business.datasource.cache.contacts.ContactEventDao
import dev.zidali.giftapp.business.datasource.cache.contacts.GiftDao
import dev.zidali.giftapp.business.datasource.datastore.AppDataStore
import dev.zidali.giftapp.business.interactors.main.contacts.DeleteContacts
import dev.zidali.giftapp.business.interactors.main.contacts.contact_detail.DeleteGifts
import dev.zidali.giftapp.business.interactors.main.contacts.contact_detail.FetchEvents
import dev.zidali.giftapp.business.interactors.main.fab.CreateContact
import dev.zidali.giftapp.business.interactors.main.contacts.contact_detail.FetchGifts
import dev.zidali.giftapp.business.interactors.main.fab.AddGift
import dev.zidali.giftapp.business.interactors.main.fab.CreateEvent
import dev.zidali.giftapp.business.interactors.main.shared.*

@Module
@InstallIn(ActivityRetainedComponent::class)
object MainModule {

    /**
     * INTERACTORS
     */

    @ActivityRetainedScoped
    @Provides
    fun provideCreateContact(
        contactDao: ContactDao,
        firebaseAuth: FirebaseAuth,
        fireStore: FirebaseFirestore,
        connectivityManager: ConnectivityManager,
    ): CreateContact {
        return CreateContact(
            contactDao,
            firebaseAuth,
            fireStore,
            connectivityManager,
        )
    }

    @ActivityRetainedScoped
    @Provides
    fun provideFetchContacts(
        contactDao: ContactDao,
        giftDao: GiftDao,
        contactEventDao: ContactEventDao,
        firebaseAuth: FirebaseAuth,
        fireStore: FirebaseFirestore,
        connectivityManager: ConnectivityManager,
        appDataStore: AppDataStore,
    ): FetchContacts {
        return FetchContacts(
            contactDao,
            giftDao,
            contactEventDao,
            firebaseAuth,
            fireStore,
            connectivityManager,
            appDataStore,
        )
    }

    @ActivityRetainedScoped
    @Provides
    fun provideFetchGifts(
        giftDao: GiftDao,
        firebaseAuth: FirebaseAuth,
        fireStore: FirebaseFirestore,
        connectivityManager: ConnectivityManager,
        appDataStore: AppDataStore,
    ): FetchGifts {
        return FetchGifts(
            giftDao,
            firebaseAuth,
            fireStore,
            connectivityManager,
            appDataStore,
        )
    }

    @ActivityRetainedScoped
    @Provides
    fun provideFetchEvents(
        contactEventDao: ContactEventDao,
        firebaseAuth: FirebaseAuth,
        fireStore: FirebaseFirestore,
        connectivityManager: ConnectivityManager,
        appDataStore: AppDataStore,
    ): FetchEvents {
        return FetchEvents(
            contactEventDao,
            firebaseAuth,
            fireStore,
            connectivityManager,
            appDataStore
        )
    }

    @ActivityRetainedScoped
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

    @ActivityRetainedScoped
    @Provides
    fun provideAddGift(
        giftDao: GiftDao,
        firebaseAuth: FirebaseAuth,
        fireStore: FirebaseFirestore,
        connectivityManager: ConnectivityManager,
    ): AddGift {
        return AddGift(
            giftDao,
            firebaseAuth,
            fireStore,
            connectivityManager,
        )
    }

    @ActivityRetainedScoped
    @Provides
    fun provideCreateEvent(
        contactEventDao: ContactEventDao,
        firebaseAuth: FirebaseAuth,
        fireStore: FirebaseFirestore,
        connectivityManager: ConnectivityManager,
    ): CreateEvent {
        return CreateEvent(
            contactEventDao,
            firebaseAuth,
            fireStore,
            connectivityManager,
        )
    }

    @ActivityRetainedScoped
    @Provides
    fun provideUpdateContact(
        contactDao: ContactDao,
        giftDao: GiftDao,
        contactEventDao: ContactEventDao,
        firebaseAuth: FirebaseAuth,
        fireStore: FirebaseFirestore,
        connectivityManager: ConnectivityManager,
    ): UpdateContact {
        return UpdateContact (
            contactDao,
            giftDao,
            contactEventDao,
            firebaseAuth,
            fireStore,
            connectivityManager,
        )
    }

    @ActivityRetainedScoped
    @Provides
    fun provideDeleteContacts(
        contactDao: ContactDao,
        firebaseAuth: FirebaseAuth,
        fireStore: FirebaseFirestore,
        connectivityManager: ConnectivityManager,
        appDataStore: AppDataStore,
    ): DeleteContacts {
        return DeleteContacts(
            contactDao,
            firebaseAuth,
            fireStore,
            connectivityManager,
            appDataStore,
        )
    }

    @ActivityRetainedScoped
    @Provides
    fun provideDeleteEvents(
        contactEventDao: ContactEventDao,
        firebaseAuth: FirebaseAuth,
        fireStore: FirebaseFirestore,
        connectivityManager: ConnectivityManager,
        appDataStore: AppDataStore,
    ): DeleteEvents {
        return DeleteEvents(
            contactEventDao,
            firebaseAuth,
            fireStore,
            connectivityManager,
            appDataStore,
        )
    }

    @ActivityRetainedScoped
    @Provides
    fun provideDeleteGifts(
        giftDao: GiftDao,
        firebaseAuth: FirebaseAuth,
        fireStore: FirebaseFirestore,
        connectivityManager: ConnectivityManager,
        appDataStore: AppDataStore,
    ): DeleteGifts {
        return DeleteGifts(
            giftDao,
            firebaseAuth,
            fireStore,
            connectivityManager,
            appDataStore,
        )
    }

    @ActivityRetainedScoped
    @Provides
    fun provideUpdateContactEventReminder(
        contactEventDao: ContactEventDao,
        firebaseAuth: FirebaseAuth,
        fireStore: FirebaseFirestore,
    ): UpdateContactEventReminder {
        return UpdateContactEventReminder(
            contactEventDao,
            firebaseAuth,
            fireStore,
        )
    }

    @ActivityRetainedScoped
    @Provides
    fun provideFetchEvent(
        contactEventDao: ContactEventDao
    ): FetchEvent {
        return FetchEvent (
            contactEventDao
        )
    }

    @ActivityRetainedScoped
    @Provides
    fun provideUpdateEvent(
        contactEventDao: ContactEventDao,
        firebaseAuth: FirebaseAuth,
        fireStore: FirebaseFirestore,
        connectivityManager: ConnectivityManager,

    ): UpdateEvent {
        return UpdateEvent (
            contactEventDao,
            firebaseAuth,
            fireStore,
            connectivityManager,
        )
    }

    @ActivityRetainedScoped
    @Provides
    fun provideUpdateGift(
        giftDao: GiftDao,
        firebaseAuth: FirebaseAuth,
        fireStore: FirebaseFirestore,
        connectivityManager: ConnectivityManager,
    ): SetIsCheckedGift {
        return SetIsCheckedGift(
            giftDao,
            firebaseAuth,
            fireStore,
            connectivityManager,
        )
    }


}