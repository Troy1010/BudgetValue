package com.tminus1010.budgetvalue.__devEnvs

import android.app.Application
import android.content.Intent
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.tminus1010.budgetvalue.AndroidTestAssetOwner
import com.tminus1010.budgetvalue.MockImportSelectionActivity
import com.tminus1010.budgetvalue._core.GetExtraMenuItemPartials
import com.tminus1010.budgetvalue._core.LaunchSelectFile
import com.tminus1010.budgetvalue._core.presentation.model.MenuVMItem
import com.tminus1010.budgetvalue._core.view.HostActivity
import com.tminus1010.budgetvalue._shared.app_init.AppInit
import com.tminus1010.budgetvalue._shared.app_init.data.AppInitRepo
import com.tminus1010.budgetvalue.replay_or_future.data.FuturesRepo
import com.tminus1010.budgetvalue.replay_or_future.models.TerminationStatus
import com.tminus1010.budgetvalue.replay_or_future.models.TotalFuture
import com.tminus1010.budgetvalue.transactions.app.TransactionsInteractor
import com.tminus1010.budgetvalue.transactions.models.Transaction
import com.tminus1010.tmcommonkotlin.misc.generateUniqueID
import com.tminus1010.tmcommonkotlin.rx.extensions.toSingle
import com.tminus1010.tmcommonkotlin.view.extensions.easyToast
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.components.SingletonComponent
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
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
        fun launchImport() = object : LaunchSelectFile() {
            override fun invoke(hostActivity: HostActivity) {
                Intent(hostActivity, MockImportSelectionActivity::class.java)
                    .also { hostActivity.startActivity(it) }
            }
        }

        @Provides
        @Singleton
        fun androidTestAssetOwner(): AndroidTestAssetOwner = object : AndroidTestAssetOwner() {
            override val assets = InstrumentationRegistry.getInstrumentation().context.assets
        }

        @Provides
        @Singleton
        fun getExtraMenuItemPartials(appInitRepo: AppInitRepo, appInit: AppInit, transactionsInteractor: TransactionsInteractor, futuresRepo: FuturesRepo, application: Application) = object : GetExtraMenuItemPartials() {
            override fun invoke() =
                arrayOf(
                    MenuVMItem("Redo App Init") {
                        appInitRepo.pushAppInitBool(false)
                            .andThen(appInit)
                            .subscribe()
                    },
                    MenuVMItem("Import Transaction for Future") {
                        futuresRepo.fetchFutures().toSingle()
                            .flatMapCompletable { futures ->
                                val firstSearchTotal = futures
                                    .find { it is TotalFuture && it.terminationStatus == TerminationStatus.WAITING_FOR_MATCH }
                                    ?.let { it as TotalFuture }
                                    ?.searchTotal
                                if (firstSearchTotal != null)
                                    transactionsInteractor.importTransactions(
                                        listOf(
                                            Transaction(
                                                date = LocalDate.of(2018, 1, 1),
                                                description = "Mock description of transaction",
                                                amount = firstSearchTotal,
                                                categoryAmounts = mapOf(),
                                                categorizationDate = null,
                                                id = generateUniqueID(),
                                            )
                                        )
                                    )
                                else
                                    Completable.fromCallable { application.easyToast("No TotalFutures found") }.subscribeOn(AndroidSchedulers.mainThread())
                            }
                            .subscribe()
                    },
                )
        }
    }
}