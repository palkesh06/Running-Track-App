package com.example.maps.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.example.maps.background.tracking.service.DefaultBackgroundTrackingManager
import com.example.maps.data.db.FitPathDb
import com.example.maps.data.db.FitPathDb.Companion.DATABASE_NAME
import com.example.maps.data.db.dao.RunDao
import com.example.maps.data.tracking.location.DefaultLocationTrackingManager
import com.example.maps.data.tracking.location.LocationUtils
import com.example.maps.data.tracking.timer.DefaultTimeTracker
import com.example.maps.domain.tracking.background.BackgroundTrackingManager
import com.example.maps.domain.tracking.location.LocationTrackingManager
import com.example.maps.domain.tracking.timer.TimeTracker
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.plus
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {
    companion object {
        private const val USER_PREFERENCES_FILE_NAME = "user_preferences"

        @Singleton
        @Provides
        fun provideFusedLocationProviderClient(
            @ApplicationContext context: Context
        ): FusedLocationProviderClient = LocationServices
            .getFusedLocationProviderClient(context)

        @Provides
        @Singleton
        fun provideRunningDB(
            @ApplicationContext context: Context
        ): FitPathDb = Room.databaseBuilder(
            context,
            FitPathDb::class.java,
            DATABASE_NAME
        ).build()

        @Singleton
        @Provides
        fun provideRunDao(db: FitPathDb): RunDao = db.getRunDao()

        @Provides
        @Singleton
        fun providesPreferenceDataStore(
            @ApplicationContext context: Context,
            @ApplicationScope scope: CoroutineScope,
            @IoDispatcher ioDispatcher: CoroutineDispatcher
        ): DataStore<Preferences> =
            PreferenceDataStoreFactory.create(
                corruptionHandler = ReplaceFileCorruptionHandler(
                    produceNewData = { emptyPreferences() }
                ),
                produceFile = { context.preferencesDataStoreFile(USER_PREFERENCES_FILE_NAME) },
                scope = scope.plus(ioDispatcher + SupervisorJob())
            )


        @Singleton
        @Provides
        fun provideLocationTrackingManager(
            @ApplicationContext context: Context,
            fusedLocationProviderClient: FusedLocationProviderClient,
        ): LocationTrackingManager {
            return DefaultLocationTrackingManager(
                fusedLocationProviderClient = fusedLocationProviderClient,
                context = context,
                locationRequest = LocationUtils.locationRequestBuilder.build()
            )
        }

    }
    @Binds
    @Singleton
    abstract fun provideBackgroundTrackingManager(
        trackingServiceManager: DefaultBackgroundTrackingManager
    ): BackgroundTrackingManager

    @Binds
    @Singleton
    abstract fun provideTimeTracker(
        timeTracker: DefaultTimeTracker
    ): TimeTracker
}