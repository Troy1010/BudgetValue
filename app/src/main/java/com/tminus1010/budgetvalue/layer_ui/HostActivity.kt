package com.tminus1010.budgetvalue.layer_ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.tminus1010.budgetvalue.CODE_PICK_TRANSACTIONS_FILE
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.dependency_injection.ViewModelProviders
import com.tminus1010.budgetvalue.dependency_injection.injection_extensions.appComponent
import com.tminus1010.budgetvalue.dependency_injection.injection_extensions.domain
import com.tminus1010.budgetvalue.dependency_injection.injection_extensions.errorHandler
import com.tminus1010.budgetvalue.dependency_injection.injection_extensions.flavorIntersection
import com.tminus1010.budgetvalue.extensions.add
import com.tminus1010.tmcommonkotlin.view.extensions.toast
import kotlinx.android.synthetic.main.activity_host.*

class HostActivity : AppCompatActivity() {
    val vmps by lazy { ViewModelProviders(this, appComponent) }
    val nav by lazy { findNavController(R.id.fragNavHost) }
    val menuItemPartials by lazy { flavorIntersection.getMenuItemPartials(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        domain.appInit()
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
        bottom_navigation.selectedItemId = R.id.menu_import
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.clear()
        menu.add(*menuItemPartials)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        menuItemPartials.find { item.itemId == it.id }!!.action()
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (requestCode == CODE_PICK_TRANSACTIONS_FILE && resultCode == Activity.RESULT_OK) {
            try {
                val inputStream = contentResolver.openInputStream(intent!!.data!!)!!
                vmps.transactionsVM.importTransactions(inputStream)
                toast("Import successful")
            } catch (e: Exception) {
                handle(e)
            }
        }
        super.onActivityResult(requestCode, resultCode, intent)
    }

    fun handle(e: Exception) = errorHandler.handle(nav, e)
}
