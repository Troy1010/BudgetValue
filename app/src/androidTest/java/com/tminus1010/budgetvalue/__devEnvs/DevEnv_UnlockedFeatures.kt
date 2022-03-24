package com.tminus1010.budgetvalue.__devEnvs

import android.content.Intent
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tminus1010.budgetvalue.MockImportSelectionActivity
import com.tminus1010.budgetvalue.ui.host.HostActivity
import com.tminus1010.budgetvalue.ui.all_features.LaunchSelectFile
import com.tminus1010.budgetvalue.app.IsPlanFeatureEnabledUC
import com.tminus1010.budgetvalue._unrestructured.reconcile.data.IsReconciliationFeatureEnabled
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.components.SingletonComponent
import io.reactivex.rxjava3.core.Observable
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Singleton

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class DevEnv_UnlockedFeatures {
    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var activityScenarioRule = ActivityScenarioRule(HostActivity::class.java)

    init {
        // TODO("Duct-tape solution b/c partial mocks are difficult")
        IsPlanFeatureEnabledUC.isPlanFeatureEnabledOverride = Observable.just(true)
        IsReconciliationFeatureEnabled.isReconciliationFeatureEnabledOverride = Observable.just(true)
    }

    @Test
    fun unlockedFeatures() {
        logz("DevEnv_UnlockedFeatures")
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