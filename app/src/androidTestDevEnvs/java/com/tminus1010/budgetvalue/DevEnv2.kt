package com.tminus1010.budgetvalue

import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tminus1010.budgetvalue._core.GetExtraMenuItemPartialsUC
import com.tminus1010.budgetvalue._core.LaunchImportUC
import com.tminus1010.budgetvalue._core.TestException
import com.tminus1010.budgetvalue._core.middleware.getBlocks
import com.tminus1010.budgetvalue._core.middleware.reflectXY
import com.tminus1010.budgetvalue._core.middleware.ui.MenuItemPartial
import com.tminus1010.budgetvalue._core.ui.HostActivity
import com.tminus1010.budgetvalue._core.ui.MockImportSelectionActivity
import com.tminus1010.tmcommonkotlin.core.logz
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.components.SingletonComponent
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.math.BigDecimal
import javax.inject.Singleton

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class DevEnv2 {
    @get:Rule var hiltRule = HiltAndroidRule(this)

    @InstallIn(SingletonComponent::class)
    @Module
    object UCModule_Mock {
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
        fun getExtraMenuItemPartialsUC() = object : GetExtraMenuItemPartialsUC() {
            override fun invoke(hostActivity: HostActivity) = hostActivity.run {
                arrayOf(
                    MenuItemPartial("Throw Test Error") {
                        hostFrag.handle(TestException())
                    },
                    MenuItemPartial("Throw Error") {
                        hostFrag.handle(Exception("Zip zoop an error"))
                    },
                    MenuItemPartial("Print Transactions") {
                        transactionsVM.transactions.take(1)
                            .subscribe { logz("transactions:${it?.joinToString(",")}") }
                    },
                    MenuItemPartial("Print Spends") {
                        // define transactionBlocks
                        val transactionBlocks = transactionsVM.transactions.blockingFirst().getBlocks(2)
                        // define stringBlocks
                        val stringBlocks = arrayListOf<HashMap<String, String>>()
                        for (transactionBlock in transactionBlocks) {
                            val curStringBlock = HashMap<String, String>()
                            stringBlocks.add(curStringBlock)
                            for (category in categoriesVM.userCategories.value) {
                                curStringBlock[category.name] = transactionBlock.value
                                    .map { it.categoryAmounts[category] ?: BigDecimal.ZERO }
                                    .fold(BigDecimal.ZERO, BigDecimal::add)
                                    .toString()
                            }
                        }
                        //
                        logz("stringBlocks:${stringBlocks}")
                        logz("stringBlocks.reflectXY():${stringBlocks.reflectXY()}")
                        val spends = HashMap<String, String>()
                        for (x in stringBlocks.reflectXY()) {
                            spends[x.key] = x.value.joinToString(",")
                        }
                        logz("spends:${spends}")
                        //
                        val column = listOf(
                            "",
                            "",
                            "",
                            spends["Default"] ?: "",
                            "",
                            "",
                            spends["Food"] ?: "",
                            spends["Drinks"] ?: "",
                            spends["Vanity Food"] ?: "",
                            spends["Improvements"] ?: "",
                            spends["Dentist"] ?: "",
                            spends["Diabetic Supplies"] ?: "",
                            spends["Leli"] ?: "",
                            spends["Misc"] ?: "",
                            spends["Gas"] ?: "",
                            "",
                            spends["Vanity Food"] ?: "",
                            spends["Emergency"] ?: ""
                        )
                        //
                        val spendsString = column.joinToString("\n")
                        logz("spendsString:${spendsString}")
                    },
                )
            }
        }
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