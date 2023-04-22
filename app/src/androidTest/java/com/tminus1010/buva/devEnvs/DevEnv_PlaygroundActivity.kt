package com.tminus1010.buva.devEnvs

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tminus1010.buva.PlaygroundActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class DevEnv_PlaygroundActivity {
    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var activityScenarioRule = ActivityScenarioRule(PlaygroundActivity::class.java)

    @Test
    fun devEnv() {
        logz("DevEnv_PlaygroundActivity")
        // # Stall forever
        while (true) Thread.sleep(5000)
    }
}