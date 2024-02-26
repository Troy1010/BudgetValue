package com.tminus1010.buva.data

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.tminus1010.buva.all_layers.DaggerAppComponent
import com.tminus1010.buva.core_testing.shared.FakeDataStore
import com.tminus1010.buva.environment.EnvironmentModule
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class SettingsRepoTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test() = runTest {
        // # Given
        val settingsRepo =
            DaggerAppComponent.builder()
                .application(mockk())
                .environmentModule2(
                    object : EnvironmentModule() {
                        override fun provideDataStore(application: Application): DataStore<Preferences> {
                            return FakeDataStore()
                        }
                    },
                )
                .build()
                .settingsRepo()
        Assertions.assertEquals(14, settingsRepo.blockSize.value)
        // # When
        settingsRepo.pushBlockSize(7)
        // # Then
        Thread.sleep(10)
        Assertions.assertEquals(7, settingsRepo.blockSize.value)
    }
}