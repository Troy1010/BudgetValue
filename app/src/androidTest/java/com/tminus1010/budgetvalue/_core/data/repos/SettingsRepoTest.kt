package com.tminus1010.budgetvalue._core.data.repos

import com.tminus1010.budgetvalue.FakeDataStore
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

@HiltAndroidTest
class SettingsRepoTest {

    @Test
    fun anchorDateOffset_default_push() {
        // # Given
        val settingsRepo = SettingsRepo(FakeDataStore())
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
        // # Given
        val settingsRepo = SettingsRepo(FakeDataStore())
        // # Then
        assertEquals(14, settingsRepo.blockSize.value)
        // # When
        runBlocking { settingsRepo.pushBlockSize(7) }
        Thread.sleep(10)
        // # Then
        assertEquals(7, settingsRepo.blockSize.value)
    }
}