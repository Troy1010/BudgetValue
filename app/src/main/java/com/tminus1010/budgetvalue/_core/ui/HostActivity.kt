package com.tminus1010.budgetvalue._core.ui

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.GetExtraMenuItemPartialsUC
import com.tminus1010.budgetvalue._core.extensions.add
import com.tminus1010.budgetvalue._core.middleware.ui.MenuVMItem
import com.tminus1010.budgetvalue._shared.app_init.AppInitDomain
import com.tminus1010.budgetvalue._shared.feature_flags.IsPlanEnabled
import com.tminus1010.budgetvalue._shared.feature_flags.IsReconcileEnabled
import com.tminus1010.budgetvalue.databinding.ActivityHostBinding
import com.tminus1010.budgetvalue.replay_or_future.FuturesReviewFrag
import com.tminus1010.budgetvalue.replay_or_future.ReplaysFrag
import com.tminus1010.budgetvalue.transactions.TransactionsMiscVM
import com.tminus1010.budgetvalue.transactions.domain.TransactionsDomain
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.rx.extensions.pairwise
import com.tminus1010.tmcommonkotlin.view.extensions.easyToast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HostActivity : AppCompatActivity() {
    private val vb by lazy { ActivityHostBinding.inflate(layoutInflater) }

    @Inject
    lateinit var getExtraMenuItemPartialsUC: GetExtraMenuItemPartialsUC

    @Inject
    lateinit var appInitDomain: AppInitDomain

    @Inject
    lateinit var transactionsDomain: TransactionsDomain

    @Inject
    lateinit var isPlanEnabled: IsPlanEnabled

    @Inject
    lateinit var isReconcileEnabled: IsReconcileEnabled

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
        //
        isPlanEnabled().pairwise().filter { it.second }.map { it.second }.observe(this) {
            AlertDialog.Builder(this)
                .setMessage("You've leveled up! You can now make Plans. We've already set it based on your history, but adjust it to fit your desires. In 2 weeks, you can Reconcile what actually happened with your Plan.")
                .setNeutralButton("Okay") { _, _ -> }
                .show()
        }
        isPlanEnabled().observe(this) { vb.bottomNavigation.menu.findItem(R.id.planFrag).isVisible = it }
        isReconcileEnabled().observe(this) { vb.bottomNavigation.menu.findItem(R.id.reconcileFrag).isVisible = it }
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
            MenuVMItem(
                title = "Transactions",
                onClick = { nav.navigate(R.id.transactionsFrag) },
            ),
            MenuVMItem(
                title = "Futures",
                onClick = { FuturesReviewFrag.navTo(nav) },
            ),
            MenuVMItem(
                title = "Replays",
                onClick = { ReplaysFrag.navTo(nav) },
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
                    easyToast("Import successful")
                } catch (e: Throwable) {
                    hostFrag.handle(e)
                }
            }
        }
}
