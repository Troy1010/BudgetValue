package com.tminus1010.budgetvalue.all_features.data.repo

import android.app.Application
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import com.tminus1010.budgetvalue.FakeDataStore
import com.tminus1010.budgetvalue.__core_testing.app
import com.tminus1010.budgetvalue.all_features.all_layers.dependency_injection.EnvironmentModule
import com.tminus1010.budgetvalue.all_features.all_layers.dependency_injection.IEnvironmentModule
import com.tminus1010.budgetvalue.all_features.data.CategoriesRepo
import com.tminus1010.budgetvalue.all_features.data.SettingsRepo
import com.tminus1010.budgetvalue.all_features.data.service.CategoryDatabase
import com.tminus1010.budgetvalue.all_features.data.service.MiscDatabase
import com.tminus1010.budgetvalue.all_features.data.service.RoomWithCategoriesTypeConverter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import javax.inject.Singleton

@UninstallModules(EnvironmentModule::class)
@HiltAndroidTest
class SettingsRepoTest {
    @Test
    fun anchorDateOffset_default_push() {
        // # Then
        assertEquals(0, settingsRepo.anchorDateOffset.value)
        // # When
        runBlocking { settingsRepo.pushAnchorDateOffset(3) }
        Thread.sleep(10)
        // # Then
        assertEquals(3, settingsRepo.anchorDateOffset.value)
    }

    @Test
    fun blockSize_default_push() {
        // # Then
        assertEquals(14, settingsRepo.blockSize.value)
        // # When
        runBlocking { settingsRepo.pushBlockSize(7) }
        Thread.sleep(10)
        // # Then
        assertEquals(7, settingsRepo.blockSize.value)
    }

    @get:Rule
    var hiltAndroidRule = HiltAndroidRule(this)

    @Inject
    lateinit var settingsRepo: SettingsRepo

    @Inject
    lateinit var categoriesRepo: CategoriesRepo

    @Before
    fun before() {
        hiltAndroidRule.inject()
    }

    @BindValue
    val fakeDataStore: DataStore<Preferences> = FakeDataStore()

    @InstallIn(SingletonComponent::class)
    @Module
    object MockModule : IEnvironmentModule {
        @Provides
        @Singleton
        override fun providesSharedPreferences(application: Application): SharedPreferences {
            return super.providesSharedPreferences(application)
        }

        @Provides
        @Singleton
        fun categoryDatabase(): CategoryDatabase {
            return Room.inMemoryDatabaseBuilder(app, CategoryDatabase::class.java).build()
        }

        @Provides
        @Singleton
        fun miscDatabase(roomWithCategoriesTypeConverter: RoomWithCategoriesTypeConverter): MiscDatabase {
            return Room.inMemoryDatabaseBuilder(app, MiscDatabase::class.java)
                .addTypeConverter(roomWithCategoriesTypeConverter)
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}