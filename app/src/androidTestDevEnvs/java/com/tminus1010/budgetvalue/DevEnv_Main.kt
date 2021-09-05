package com.tminus1010.budgetvalue

import android.content.Intent
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tminus1010.budgetvalue._core.GetExtraMenuItemPartialsUC
import com.tminus1010.budgetvalue._core.LaunchImportUC
import com.tminus1010.budgetvalue._core.TestException
import com.tminus1010.budgetvalue._core.middleware.ui.MenuVMItem
import com.tminus1010.budgetvalue._core.ui.HostActivity
import com.tminus1010.budgetvalue._core.ui.MockImportSelectionActivity
import com.tminus1010.budgetvalue._shared.app_init.AppInitDomain
import com.tminus1010.budgetvalue._shared.app_init.data.AppInitRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.components.SingletonComponent
import io.reactivex.rxjava3.core.Completable
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
        // # Stall forever
        while (true) Thread.sleep(5000)
    }

    @InstallIn(SingletonComponent::class)
    @Module
    object MockModule {
        @Provides
        @Singleton
        fun launchImportUC() = object : LaunchImportUC() {
            override fun invoke(hostActivity: HostActivity) {
                Intent(hostActivity, MockImportSelectionActivity::class.java)
                    .also { hostActivity.startActivity(it) }
            }
        }

        @Provides
        @Singleton
        fun getExtraMenuItemPartialsUC(appInitRepo: AppInitRepo, appInitDomain: AppInitDomain) = object : GetExtraMenuItemPartialsUC() {
            override fun invoke(hostActivity: HostActivity) = hostActivity.run {
                arrayOf(
                    MenuVMItem("Redo App Init") {
                        appInitRepo.pushAppInitBool(false)
                            .andThen(Completable.fromAction { appInitDomain.appInit() })
                            .subscribe()
                    },
                    MenuVMItem("Throw Test Error") {
                        hostFrag.handle(TestException())
                    },
                    MenuVMItem("Throw Error") {
                        hostFrag.handle(Exception("Zip zoop an error"))
                    },
                )
            }
        }
    }
}