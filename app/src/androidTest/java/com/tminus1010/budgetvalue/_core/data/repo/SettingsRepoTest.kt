package com.tminus1010.budgetvalue._core.data.repo

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import com.tminus1010.budgetvalue.FakeDataStore
import com.tminus1010.budgetvalue.__core_testing.app
import com.tminus1010.budgetvalue._core.all.dependency_injection.DataStoreModule
import com.tminus1010.budgetvalue._core.data.CategoryDatabase
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@UninstallModules(DataStoreModule::class)
@HiltAndroidTest
class SettingsRepoTest {
    @get:Rule
    var hiltAndroidRule = HiltAndroidRule(this)

    @BindValue
    val categoryDatabase: CategoryDatabase =
        Room.inMemoryDatabaseBuilder(app, CategoryDatabase::class.java).build()

    @BindValue
    val fakeDataStore: DataStore<Preferences> = FakeDataStore()

    @Inject
    lateinit var settingsRepo: SettingsRepo

    @Before
    fun before() {
        hiltAndroidRule.inject()
    }

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
}