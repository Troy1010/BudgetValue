package com.tminus1010.budgetvalue.layer_ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.tminus1010.budgetvalue.CODE_PICK_TRANSACTIONS_FILE
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.dependency_injection.ViewModelProviders
import com.tminus1010.budgetvalue.dependency_injection.injection_extensions.appComponent
import com.tminus1010.budgetvalue.dependency_injection.injection_extensions.domain
import com.tminus1010.budgetvalue.dependency_injection.injection_extensions.flavorIntersection
import com.tminus1010.budgetvalue.extensions.add
import com.tminus1010.tmcommonkotlin.view.extensions.toast
import kotlinx.android.synthetic.main.activity_host.*

class HostActivity : AppCompatActivity(), IViewModels {
    override val viewModelProviders by lazy { ViewModelProviders(this, appComponent) }
    val hostFrag by lazy { fragNavHost as HostFrag }
    val nav by lazy { findNavController(R.id.fragNavHost) }
    val menuItemPartials by lazy { flavorIntersection.getMenuItemPartials(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        domain.appInit()
        setContentView(R.layout.activity_host)
        // # Bind bottom menu to navigation.
        // In order for NavigationUI.setupWithNavController to work, the ids in
        // R.menu.* must exactly match R.navigation.*
        NavigationUI.setupWithNavController(bottom_navigation, nav)
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
                transactionsVM.importTransactions(inputStream)
                toast("Import successful")
            } catch (e: Exception) {
                hostFrag.handle(e)
            }
        }
        super.onActivityResult(requestCode, resultCode, intent)
    }
}
