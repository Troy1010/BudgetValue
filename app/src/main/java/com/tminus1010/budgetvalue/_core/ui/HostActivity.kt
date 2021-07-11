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
import com.tminus1010.budgetvalue._core.middleware.ui.MenuItemPartial
import com.tminus1010.budgetvalue._core.middleware.ui.viewBinding
import com.tminus1010.budgetvalue._shared.app_init.AppInitDomain
import com.tminus1010.budgetvalue.databinding.ActivityHostBinding
import com.tminus1010.budgetvalue.transactions.TransactionsMiscVM
import com.tminus1010.tmcommonkotlin.view.extensions.toast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HostActivity : AppCompatActivity() {
    private val vb by viewBinding(ActivityHostBinding::inflate)

    @Inject
    lateinit var getExtraMenuItemPartialsUC: GetExtraMenuItemPartialsUC

    @Inject
    lateinit var appInitDomain: AppInitDomain
    private val transactionsMiscVM: TransactionsMiscVM by viewModels()
    val hostFrag by lazy { supportFragmentManager.findFragmentById(R.id.frag_nav_host) as HostFrag }
    private val nav by lazy { findNavController(R.id.frag_nav_host) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(vb.root)
        // # Initialize app once per install
        appInitDomain.appInit()
        // # Bind bottom menu to navigation.
        // In order for NavigationUI.setupWithNavController to work, the ids in R.menu.* must exactly match R.navigation.*
        NavigationUI.setupWithNavController(vb.bottomNavigation, hostFrag.navController)
        //
        // This line solves (after doing an Import): java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
        transactionsMiscVM
    }

    override fun onStart() {
        super.onStart()
        nav.addOnDestinationChangedListener { _, navDestination, _ ->
            Log.d("budgetvalue.Nav", "${navDestination.label}")
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.clear()
        menu.add(
            MenuItemPartial(
                title = "Transactions",
                lambda = { nav.navigate(R.id.transactionsFrag) },
            ),
            *getExtraMenuItemPartialsUC(this)
        )
        return true
    }

    val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                try {
                    contentResolver.openInputStream(result.data!!.data!!)!!
                        .also { inputStream -> transactionsMiscVM.userImportTransactions(inputStream) }
                    toast("Import successful")
                } catch (e: Throwable) {
                    hostFrag.handle(e)
                }
            }
        }
}
