package com.tminus1010.budgetvalue.__devEnvs

import android.content.Intent
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tminus1010.budgetvalue.MockImportSelectionActivity
import com.tminus1010.budgetvalue.ui.host.HostActivity
import com.tminus1010.budgetvalue.ui.host.LaunchChooseFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.components.SingletonComponent
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Singleton

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class DevEnv_Main {
    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var activityScenarioRule = ActivityScenarioRule(HostActivity::class.java)

    @Test
    fun main() {
        logz("DevEnv_Main")
        // # Stall forever
        while (true) Thread.sleep(5000)
    }

    @InstallIn(SingletonComponent::class)
    @Module
    object MockModule {
        @Provides
        @Singleton
        fun launchImport() = object : LaunchChooseFile() {
            override fun invoke(hostActivity: HostActivity) {
                hostActivity.startActivity(
                    Intent(hostActivity, MockImportSelectionActivity::class.java)
                )
            }
        }
    }
}