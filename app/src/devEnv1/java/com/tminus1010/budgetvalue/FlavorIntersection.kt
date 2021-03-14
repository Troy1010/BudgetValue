package com.tminus1010.budgetvalue

import android.app.Activity
import android.content.Intent
import com.tminus1010.budgetvalue.dependency_injection.injection_extensions.domain
import com.tminus1010.budgetvalue.dependency_injection.injection_extensions.repo
import com.tminus1010.budgetvalue.layer_ui.HostActivity
import com.tminus1010.budgetvalue.layer_ui.misc.MenuItemPartial
import com.tminus1010.tmcommonkotlin.misc.logz
import com.tminus1010.tmcommonkotlin.rx.extensions.launch
import com.tminus1010.tmcommonkotlin.view.extensions.toast
import java.math.BigDecimal
import javax.inject.Inject

class FlavorIntersection @Inject constructor(): IFlavorIntersection {
    override fun getMenuItemPartials(activity: HostActivity): Array<MenuItemPartial> {
        return activity.run {
            arrayOf(
                MenuItemPartial("Throw Test Error") {
                    hostFrag.handle(TestException())
                },
                MenuItemPartial("Throw Error") {
                    hostFrag.handle(Exception("Zip zoop an error"))
                },
                MenuItemPartial("Import Transactions") {
                    launchImport(activity)
                },
                MenuItemPartial("Print Transactions") {
                    vmps.transactionsVM.transactions.take(1)
                        .subscribe { logz("transactions:${it?.joinToString(",")}") }
                },
                MenuItemPartial("Print Spends") {
                    // define transactionBlocks
                    val transactionBlocks = vmps.transactionsVM.transactions.blockingFirst().getBlocks(2)
                    // define stringBlocks
                    val stringBlocks = arrayListOf<HashMap<String, String>>()
                    for (transactionBlock in transactionBlocks) {
                        val curStringBlock = HashMap<String, String>()
                        stringBlocks.add(curStringBlock)
                        for (category in domain.activeCategories.value) {
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
                MenuItemPartial("AppInitBool = false") {
                    toast("AppInitBool = false")
                    repo.pushAppInitBool(false).launch()
                },
                MenuItemPartial("Debug Do Something") {
                    toast("Debug Do Something")
                    domain.reconciliations.take(1).subscribe()
                },
            )
        }
    }

    override fun launchImport(activity: Activity) {
        Intent(activity, MockImportSelectionActivity::class.java)
            .also { activity.startActivity(it) }
    }
}