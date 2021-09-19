package com.tminus1010.budgetvalue._core.presentation_and_view.host

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
import com.tminus1010.budgetvalue._core.extensions.unCheckAllMenuItems
import com.tminus1010.budgetvalue._core.middleware.Toaster
import com.tminus1010.budgetvalue._core.presentation_and_view._extensions.easyAlertDialog
import com.tminus1010.budgetvalue._core.presentation_and_view._extensions.getString
import com.tminus1010.budgetvalue._shared.app_init.AppInit
import com.tminus1010.budgetvalue.all.app.interactors.SetActivePlanFromHistory
import com.tminus1010.budgetvalue.all.data.repos.ImportTransactions
import com.tminus1010.budgetvalue.all.data.repos.IsPlanFeatureEnabled
import com.tminus1010.budgetvalue.all.data.repos.IsReconciliationFeatureEnabled
import com.tminus1010.budgetvalue.databinding.ActivityHostBinding
import com.tminus1010.budgetvalue.history.HistoryFrag
import com.tminus1010.budgetvalue.replay_or_future.FuturesReviewFrag
import com.tminus1010.budgetvalue.replay_or_future.ReplaysFrag
import com.tminus1010.budgetvalue.transactions.TransactionsMiscVM
import com.tminus1010.budgetvalue.transactions.ui.TransactionsFrag
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HostActivity : AppCompatActivity() {
    private val vb by lazy { ActivityHostBinding.inflate(layoutInflater) }
    private val hostVM by viewModels<HostVM>()

    @Inject
    lateinit var appInit: AppInit

    @Inject
    lateinit var isPlanFeatureEnabled: IsPlanFeatureEnabled

    @Inject
    lateinit var isReconciliationFeatureEnabled: IsReconciliationFeatureEnabled

    @Inject
    lateinit var toaster: Toaster

    @Inject
    lateinit var setActivePlanFromHistory: SetActivePlanFromHistory

    @Inject
    lateinit var importTransactions: ImportTransactions

    private val transactionsMiscVM: TransactionsMiscVM by viewModels()
    val hostFrag by lazy { supportFragmentManager.findFragmentById(R.id.frag_nav_host) as HostFrag }
    private val nav by lazy { findNavController(R.id.frag_nav_host) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(vb.root)
        // # Initialize app once per install
        appInit.subscribe()
        // # Bind bottom menu to navigation.
        // In order for NavigationUI.setupWithNavController to work, the ids in R.menu.* must exactly match R.navigation.*
        NavigationUI.setupWithNavController(vb.bottomNavigation, hostFrag.navController)
        //
        // This line solves (after doing an Import): java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
        transactionsMiscVM
        //
        hostVM.navToReplays.observe(this) { ReplaysFrag.navTo(nav) }
        hostVM.navToFutures.observe(this) { FuturesReviewFrag.navTo(nav) }
        hostVM.navToTransactions.observe(this) { TransactionsFrag.navTo(nav) }
        hostVM.navToHistory.observe(this) { HistoryFrag.navTo(nav) }
        hostVM.unCheckAllMenuItems.observe(this) { vb.bottomNavigation.menu.unCheckAllMenuItems() }
        //
        isPlanFeatureEnabled.onChangeToTrue.observe(this) {
            setActivePlanFromHistory.subscribe()
            easyAlertDialog(getString(hostVM.levelUpPlan))
        }
        isReconciliationFeatureEnabled.onChangeToTrue.observe(this) {
            easyAlertDialog(getString(hostVM.levelUpReconciliation))
        }
        isPlanFeatureEnabled.observe(this) { vb.bottomNavigation.menu.findItem(R.id.planFrag).isVisible = it }
        isReconciliationFeatureEnabled.observe(this) { vb.bottomNavigation.menu.findItem(R.id.reconcileFrag).isVisible = it }
    }

    override fun onStart() {
        super.onStart()
        nav.addOnDestinationChangedListener { _, navDestination, _ -> Log.d("budgetvalue.Nav", "${navDestination.label}") }
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        hostVM.topMenuVMItems.bind(menu)
        return true
    }

    val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                try {
                    importTransactions(result.data!!.data!!).subscribe()
                    toaster.toast(R.string.import_successful)
                } catch (e: Throwable) {
                    hostFrag.handle(e)
                }
            }
        }
}