package com.tminus1010.buva.data

import androidx.test.core.app.ApplicationProvider
import com.tminus1010.buva.all_layers.DaggerAppComponent
import com.tminus1010.buva.core_testing.BaseFakeEnvironmentModule
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

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

    lateinit var settingsRepo: SettingsRepo

    @Before
    fun before() {
        val component =
            DaggerAppComponent.builder()
                .environmentModule(BaseFakeEnvironmentModule())
                .application(ApplicationProvider.getApplicationContext())
                .build()
        settingsRepo = component.settingsRepo()
    }
}