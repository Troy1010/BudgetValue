package com.tminus1010.budgetvalue.layer_ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.tminus1010.budgetvalue.CODE_PICK_TRANSACTIONS_FILE
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.databinding.ActivityHostBinding
import com.tminus1010.budgetvalue.dependency_injection.ViewModelProviders
import com.tminus1010.budgetvalue.dependency_injection.injection_extensions.appComponent
import com.tminus1010.budgetvalue.dependency_injection.injection_extensions.domain
import com.tminus1010.budgetvalue.dependency_injection.injection_extensions.flavorIntersection
import com.tminus1010.budgetvalue.extensions.add
import com.tminus1010.budgetvalue.layer_ui.misc.viewBinding
import com.tminus1010.tmcommonkotlin.view.extensions.toast

class HostActivity : AppCompatActivity(), IViewModels {
    override val viewModelProviders by lazy { ViewModelProviders(this, appComponent) }
    val binding by viewBinding(ActivityHostBinding::inflate)
    val hostFrag by lazy { supportFragmentManager.findFragmentById(R.id.frag_nav_host) as HostFrag }
    val nav by lazy { findNavController(R.id.frag_nav_host) }
    val menuItemPartials by lazy { flavorIntersection.getExtraMenuItemPartials(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        domain.appInit()
        setContentView(binding.root)
        // # Bind bottom menu to navigation.
        // In order for NavigationUI.setupWithNavController to work, the ids in R.menu.* must exactly match R.navigation.*
        NavigationUI.setupWithNavController(binding.bottomNavigation, nav)
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
