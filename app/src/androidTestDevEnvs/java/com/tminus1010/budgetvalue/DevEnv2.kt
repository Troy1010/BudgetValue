package com.tminus1010.budgetvalue

import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tminus1010.budgetvalue._core.dependency_injection.HelloModule
import com.tminus1010.budgetvalue._core.ui.HostActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Singleton
import androidx.test.ext.junit.rules.ActivityScenarioRule
import dagger.hilt.android.testing.HiltAndroidRule
import org.junit.Rule

@HiltAndroidTest
@UninstallModules(HelloModule::class)
@RunWith(AndroidJUnit4::class)
class DevEnv2 {
    @get:Rule var hiltRule = HiltAndroidRule(this)

    @InstallIn(SingletonComponent::class)
    @Module
    object MockHelloModule2 {
        @Provides
        @Singleton
        fun provideHello(): String =
            "HelloFrom${javaClass.simpleName}"
    }

    @get:Rule
    var activityScenarioRule = ActivityScenarioRule(HostActivity::class.java)

    @Test
    fun devEnv2() {
        activityScenarioRule.scenario.moveToState(Lifecycle.State.RESUMED)
        // # Stall forever
        while (true) Thread.sleep(5000)
    }
}