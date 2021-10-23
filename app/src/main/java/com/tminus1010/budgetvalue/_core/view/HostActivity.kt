package com.tminus1010.budgetvalue._core.view

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
import com.tminus1010.budgetvalue._core.all.extensions.unCheckAllMenuItems
import com.tminus1010.budgetvalue.importZ.view.services.LaunchSelectFile
import com.tminus1010.budgetvalue._core.middleware.Toaster
import com.tminus1010.budgetvalue._core.presentation.view_model.HostVM
import com.tminus1010.budgetvalue._core.presentation_and_view._extensions.easyAlertDialog
import com.tminus1010.budgetvalue._core.presentation_and_view._extensions.getString
import com.tminus1010.budgetvalue.accounts.presentation.AccountsVM
import com.tminus1010.budgetvalue.importZ.data.ImportTransactions
import com.tminus1010.budgetvalue.plans.app.convenience_service.IsPlanFeatureEnabledUC
import com.tminus1010.budgetvalue.reconcile.data.IsReconciliationFeatureEnabled
import com.tminus1010.budgetvalue.app_init.AppInteractor
import com.tminus1010.budgetvalue.databinding.ActivityHostBinding
import com.tminus1010.budgetvalue.history.HistoryFrag
import com.tminus1010.budgetvalue.plans.app.convenience_service.SetActivePlanFromHistoryUC
import com.tminus1010.budgetvalue.replay_or_future.view.FuturesReviewFrag
import com.tminus1010.budgetvalue.replay_or_future.view.ReplaysFrag
import com.tminus1010.budgetvalue.transactions.view.TransactionListFrag
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HostActivity : AppCompatActivity() {
    private val vb by lazy { ActivityHostBinding.inflate(layoutInflater) }
    private val hostVM by viewModels<HostVM>()
    private val accountsVM by viewModels<AccountsVM>()

    @Inject
    lateinit var appInteractor: AppInteractor

    @Inject
    lateinit var isPlanFeatureEnabledUC: IsPlanFeatureEnabledUC

    @Inject
    lateinit var isReconciliationFeatureEnabled: IsReconciliationFeatureEnabled

    @Inject
    lateinit var toaster: Toaster

    @Inject
    lateinit var setActivePlanFromHistoryUC: SetActivePlanFromHistoryUC

    @Inject
    lateinit var importTransactions: ImportTransactions

    @Inject
    lateinit var launchSelectFile: LaunchSelectFile

    val hostFrag by lazy { supportFragmentManager.findFragmentById(R.id.frag_nav_host) as HostFrag }
    private val nav by lazy { findNavController(R.id.frag_nav_host) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(vb.root)
        // # Initialize app once per install
        appInteractor.subscribe()
        // # Bind bottom menu to navigation.
        // In order for NavigationUI.setupWithNavController to work, the ids in R.menu.* must exactly match R.navigation.*
        NavigationUI.setupWithNavController(vb.bottomNavigation, hostFrag.navController)
        //
        vb.bottomNavigation.selectedItemId = R.id.reviewFrag
        // # Events
        accountsVM.navToSelectFile.observe(this) { launchSelectFile(this) }
        isPlanFeatureEnabledUC.onChangeToTrue.observe(this) {
            setActivePlanFromHistoryUC.subscribe()
            easyAlertDialog(getString(hostVM.levelUpPlan))
        }
        isReconciliationFeatureEnabled.onChangeToTrue.observe(this) {
            easyAlertDialog(getString(hostVM.levelUpReconciliation))
        }
        hostVM.navToReplays.observe(this) { ReplaysFrag.navTo(nav) }
        hostVM.navToFutures.observe(this) { FuturesReviewFrag.navTo(nav) }
        hostVM.navToTransactions.observe(this) { TransactionListFrag.navTo(nav) }
        hostVM.navToHistory.observe(this) { HistoryFrag.navTo(nav) }
        hostVM.unCheckAllMenuItems.observe(this) { vb.bottomNavigation.menu.unCheckAllMenuItems() }
        // # State
        isPlanFeatureEnabledUC.observe(this) { vb.bottomNavigation.menu.findItem(R.id.planFrag).isVisible = it }
        isReconciliationFeatureEnabled.observe(this) { vb.bottomNavigation.menu.findItem(R.id.reconciliationHostFrag).isVisible = it }
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