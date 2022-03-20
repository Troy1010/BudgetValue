package com.tminus1010.budgetvalue.__devEnvs

import android.content.Intent
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tminus1010.budgetvalue.MockImportSelectionActivity
import com.tminus1010.budgetvalue.__core_testing.MiscTestModule
import com.tminus1010.budgetvalue._core.view.HostActivity
import com.tminus1010.budgetvalue.importZ.view.services.LaunchSelectFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Singleton

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@UninstallModules(MiscTestModule::class)
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
        fun launchImport() = object : LaunchSelectFile() {
            override fun invoke(hostActivity: HostActivity) {
                hostActivity.startActivity(
                    Intent(hostActivity, MockImportSelectionActivity::class.java)
                )
            }
        }
    }
}