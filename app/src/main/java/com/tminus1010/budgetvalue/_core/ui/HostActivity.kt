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
import com.tminus1010.budgetvalue._core.GetExtraMenuItemPartials
import com.tminus1010.budgetvalue._core.extensions.add
import com.tminus1010.budgetvalue._core.extensions.isZero
import com.tminus1010.budgetvalue._core.extensions.toMoneyBigDecimal
import com.tminus1010.budgetvalue._core.extensions.unCheckAllMenuItems
import com.tminus1010.budgetvalue._core.middleware.Toaster
import com.tminus1010.budgetvalue._core.middleware.presentation.MenuVMItem
import com.tminus1010.budgetvalue._core.models.CategoryAmounts
import com.tminus1010.budgetvalue._shared.app_init.AppInitDomain
import com.tminus1010.budgetvalue.all.data.IsPlanFeatureEnabled
import com.tminus1010.budgetvalue.all.data.IsReconciliationFeatureEnabled
import com.tminus1010.budgetvalue.databinding.ActivityHostBinding
import com.tminus1010.budgetvalue.history.HistoryFrag
import com.tminus1010.budgetvalue.plans.data.PlansRepo
import com.tminus1010.budgetvalue.plans.domain.ActivePlanDomain
import com.tminus1010.budgetvalue.replay_or_future.FuturesReviewFrag
import com.tminus1010.budgetvalue.replay_or_future.ReplaysFrag
import com.tminus1010.budgetvalue.transactions.TransactionsMiscVM
import com.tminus1010.budgetvalue.transactions.domain.TransactionsDomain
import com.tminus1010.budgetvalue.transactions.ui.TransactionsFrag
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

@AndroidEntryPoint
class HostActivity : AppCompatActivity() {
    private val vb by lazy { ActivityHostBinding.inflate(layoutInflater) }

    @Inject
    lateinit var getExtraMenuItemPartials: GetExtraMenuItemPartials

    @Inject
    lateinit var appInitDomain: AppInitDomain

    @Inject
    lateinit var transactionsDomain: TransactionsDomain

    @Inject
    lateinit var isPlanFeatureEnabled: IsPlanFeatureEnabled

    @Inject
    lateinit var isReconciliationFeatureEnabled: IsReconciliationFeatureEnabled

    @Inject
    lateinit var activePlanDomain: ActivePlanDomain

    @Inject
    lateinit var plansRepo: PlansRepo

    @Inject
    lateinit var toaster: Toaster

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
        isPlanFeatureEnabled.onChangeToTrue.observe(this) {
            Observable.combineLatest(activePlanDomain.activePlan, transactionsDomain.transactionBlocks)
            { activePlan, transactionBlocks ->
                val relevantTransactionBlocks = transactionBlocks.filter { it.defaultAmount.isZero }
                val categoryAmounts =
                    relevantTransactionBlocks
                        .fold(CategoryAmounts()) { acc, v -> acc.addTogether(v.categoryAmounts) }
                        .mapValues { (_, v) -> (v / relevantTransactionBlocks.size.toBigDecimal()).toString().toMoneyBigDecimal() }
                plansRepo.updatePlan(activePlan.copy(categoryAmounts = categoryAmounts))
            }.flatMapCompletable { it }.subscribe()
            AlertDialog.Builder(this)
                .setMessage("You've leveled up! You can now make Plans. We've already set it based on your history, but you can adjust it.\nLater on, you can Reconcile what actually happened with your Plan.")
                .setNeutralButton("Okay") { _, _ -> }
                .setCancelable(false)
                .show()
        }
        isReconciliationFeatureEnabled.onChangeToTrue.observe(this) {
            AlertDialog.Builder(this)
                .setMessage("You've leveled up! You can now make Reconciliations.")
                .setNeutralButton("Okay") { _, _ -> }
                .setCancelable(false)
                .show()
        }
        isPlanFeatureEnabled.observe(this) { vb.bottomNavigation.menu.findItem(R.id.planFrag).isVisible = it }
        isReconciliationFeatureEnabled.observe(this) { vb.bottomNavigation.menu.findItem(R.id.reconcileFrag).isVisible = it }
    }

    override fun onStart() {
        super.onStart()
        nav.addOnDestinationChangedListener { _, navDestination, _ -> Log.d("budgetvalue.Nav", "${navDestination.label}") }
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.clear()
        menu.add(
            MenuVMItem(
                title = "History",
                onClick = { HistoryFrag.navTo(nav); vb.bottomNavigation.menu.unCheckAllMenuItems() },
            ),
            MenuVMItem(
                title = "Transactions",
                onClick = { TransactionsFrag.navTo(nav); vb.bottomNavigation.menu.unCheckAllMenuItems() },
            ),
            MenuVMItem(
                title = "Futures",
                onClick = { FuturesReviewFrag.navTo(nav); vb.bottomNavigation.menu.unCheckAllMenuItems() },
            ),
            MenuVMItem(
                title = "Replays",
                onClick = { ReplaysFrag.navTo(nav); vb.bottomNavigation.menu.unCheckAllMenuItems() },
            ),
            *getExtraMenuItemPartials(this)
        )
        return true
    }

    val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                try {
                    contentResolver.openInputStream(result.data!!.data!!)!!
                        .also { inputStream -> transactionsMiscVM.userImportTransactions(inputStream) }
                    toaster.toast(R.string.import_successful)
                } catch (e: Throwable) {
                    hostFrag.handle(e)
                }
            }
        }
}
