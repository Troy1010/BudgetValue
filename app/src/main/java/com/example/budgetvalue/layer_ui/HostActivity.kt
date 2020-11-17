package com.example.budgetvalue.layer_ui

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.navigation.findNavController
import com.example.budgetvalue.*
import com.example.budgetvalue.getBlocks
import com.example.budgetvalue.reflectXY
import com.tminus1010.tmcommonkotlin.misc.toast
import com.tminus1010.tmcommonkotlin.logz.logz
import com.tminus1010.tmcommonkotlin.misc.createVmFactory
import kotlinx.android.synthetic.main.activity_host.*
import java.math.BigDecimal
import kotlin.collections.HashMap
import kotlin.time.ExperimentalTime

class HostActivity : AppCompatActivity() {
    val appComponent by lazy { (applicationContext as App).appComponent }
    val transactionsVM: TransactionsVM by viewModels { createVmFactory { TransactionsVM(appComponent.getRepo()) } }
    val navController by lazy { findNavController(R.id.fragNavHost) }

    val categoriesVM: CategoriesVM by viewModels { createVmFactory { CategoriesVM() } }
    val accountsVM: AccountsVM by viewModels { createVmFactory { AccountsVM(appComponent.getRepo()) }}
    val reconcileVM: ReconcileVM by viewModels { createVmFactory { ReconcileVM(appComponent.getRepo(), categoriesVM, transactionsVM.spends, accountsVM.accounts ) } }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host)
        bottom_navigation.setOnNavigationItemSelectedListener {
            var bSuccessfulNavigation = true
            when (it.itemId) {
                R.id.menu_accounts -> navController.navigate(R.id.accountsFrag)
                R.id.menu_actual -> navController.navigate(R.id.categorizeSpendsFrag)
                R.id.menu_reconcile -> navController.navigate(R.id.splitFrag)
                else -> bSuccessfulNavigation = false
            }
            bSuccessfulNavigation
        }
        bottom_navigation.selectedItemId = R.id.menu_reconcile
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_menu, menu)
        return true
    }

    @ExperimentalTime
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_import_transactions -> {
                val intent = Intent()
                intent.type = "*/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(
                    Intent.createChooser(intent, "Select transactions csv"),
                    CODE_PICK_TRANSACTIONS_FILE
                )
            }
            R.id.menu_import_debug_transactions -> {
                val inputStream = assets.open("transactions_2013487188.csv").buffered()
                transactionsVM.importTransactions(inputStream)
            }
            R.id.menu_print_transactions -> {
                transactionsVM.transactions.value.let {
                    logz("transactions:${it?.joinToString(",")}")
                }
            }
            R.id.menu_print_spends -> {
                // define transactionBlocks
                val transactionBlocks = transactionsVM.transactions.value.getBlocks(2)
                // define stringBlocks
                val stringBlocks = arrayListOf<HashMap<String, String>>()
                for (transactionBlock in transactionBlocks) {
                    val curStringBlock = HashMap<String, String>()
                    stringBlocks.add(curStringBlock)
                    for (category in categoriesVM.categories.value) {
                        curStringBlock[category.name] = transactionBlock.value
                            .map { it.categoryAmounts[category.name] ?: BigDecimal.ZERO }
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
