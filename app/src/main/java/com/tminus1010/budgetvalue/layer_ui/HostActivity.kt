package com.tminus1010.budgetvalue.layer_ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.tminus1010.budgetvalue.*
import com.tminus1010.budgetvalue.extensions.viewModels2
import com.tminus1010.tmcommonkotlin.logz.logz
import com.tminus1010.tmcommonkotlin.misc.toast
import kotlinx.android.synthetic.main.activity_host.*
import java.math.BigDecimal
import kotlin.time.ExperimentalTime

class HostActivity : AppCompatActivity() {
    val app by lazy { application as App }
    val transactionsVM: TransactionsVM by viewModels2 { TransactionsVM(app.appComponent.getRepo(), app.appComponent.getDatePeriodGetter()) }
    val nav by lazy { findNavController(R.id.fragNavHost) }
    val repo by lazy { app.appComponent.getRepo() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host)
        bottom_navigation.setOnNavigationItemSelectedListener {
            var bSuccessfulNavigation = true
            when (it.itemId) {
                R.id.menu_import -> nav.navigate(R.id.importFrag)
                R.id.menu_plan -> nav.navigate(R.id.planFrag)
                R.id.menu_categorize -> nav.navigate(R.id.categorizeFrag)
                R.id.menu_reconcile -> nav.navigate(R.id.reconcileFrag)
                R.id.menu_history -> nav.navigate(R.id.historyFrag)
                else -> bSuccessfulNavigation = false
            }
            bSuccessfulNavigation
        }
        // # Start at..
        bottom_navigation.selectedItemId = R.id.menu_history
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_menu, menu)
        return true
    }

    @ExperimentalTime
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_import_transactions -> {
                Intent().apply { type = "*/*"; action = Intent.ACTION_GET_CONTENT }.also {
                    startActivityForResult(
                        Intent.createChooser(it, "Select transactions csv"),
                        CODE_PICK_TRANSACTIONS_FILE
                    )
                }
            }
            R.id.menu_print_transactions -> {
                transactionsVM.transactions.take(1).subscribe {
                    logz("transactions:${it?.joinToString(",")}")
                }
            }
            R.id.menu_print_spends -> {
                // define transactionBlocks
                val transactionBlocks = transactionsVM.transactions.blockingFirst().getBlocks(2)
                // define stringBlocks
                val stringBlocks = arrayListOf<HashMap<String, String>>()
                for (transactionBlock in transactionBlocks) {
                    val curStringBlock = HashMap<String, String>()
                    stringBlocks.add(curStringBlock)
                    for (category in repo.activeCategories.value) {
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
            }
            R.id.menu_save_reconciliation -> {
                toast("Reconciliation Saved")
            }
            R.id.menu_debug_do_something -> {
                toast("Debug Do Something")
                repo.reconciliations.take(1).subscribe()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (requestCode == CODE_PICK_TRANSACTIONS_FILE && resultCode == Activity.RESULT_OK) {
            try {
                val inputStream = contentResolver.openInputStream(intent!!.data!!)!!
                transactionsVM.importTransactions(inputStream)
                toast("Import successful")
            } catch (e: Exception) {
                e.printStackTrace()
                toast("Import failed")
            }
        }
        super.onActivityResult(requestCode, resultCode, intent)
    }
}
