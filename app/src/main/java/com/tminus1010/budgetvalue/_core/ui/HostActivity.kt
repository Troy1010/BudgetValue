package com.tminus1010.budgetvalue._core.ui

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.GetExtraMenuItemPartialsUC
import com.tminus1010.budgetvalue._core.extensions.add
import com.tminus1010.budgetvalue._core.middleware.ui.viewBinding
import com.tminus1010.budgetvalue._shared.app_init.AppInitDomain
import com.tminus1010.budgetvalue.categories.CategoriesVM
import com.tminus1010.budgetvalue.databinding.ActivityHostBinding
import com.tminus1010.budgetvalue.transactions.TransactionsVM
import com.tminus1010.tmcommonkotlin.view.extensions.toast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HostActivity : AppCompatActivity() {
    @Inject lateinit var getExtraMenuItemPartialsUC: GetExtraMenuItemPartialsUC
    @Inject lateinit var appInitDomain: AppInitDomain
    val transactionsVM by viewModels<TransactionsVM>()
    val categoriesVM by viewModels<CategoriesVM>()
    val vb by viewBinding(ActivityHostBinding::inflate)
    val hostFrag by lazy { supportFragmentManager.findFragmentById(R.id.frag_nav_host) as HostFrag }
    val menuItemPartials by lazy { getExtraMenuItemPartialsUC(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(vb.root)
        // # Initialize app once per install
        appInitDomain.appInit()
        // # Bind bottom menu to navigation.
        // In order for NavigationUI.setupWithNavController to work, the ids in R.menu.* must exactly match R.navigation.*
        NavigationUI.setupWithNavController(vb.bottomNavigation, hostFrag.navController)
        //
        // This line solves (after IMPORT): java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
        transactionsVM
    }

    override fun onStart() {
        super.onStart()
        findNavController(R.id.frag_nav_host)
            .addOnDestinationChangedListener { _, navDestination, _ ->
                Log.d("budgetvalue.Nav", "${navDestination.label}")
            }
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.clear()
        menu.add(*menuItemPartials)
        return true
    }

    val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                try {
                    contentResolver.openInputStream(result.data!!.data!!)!!
                        .also { inputStream -> transactionsVM.importTransactions(inputStream) }
                    toast("Import successful")
                } catch (e: Throwable) {
                    hostFrag.handle(e)
                }
            }
        }
}