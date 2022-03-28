package com.tminus1010.budgetvalue.ui.host

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._unrestructured.history.HistoryFrag
import com.tminus1010.budgetvalue._unrestructured.reconcile.data.IsReconciliationFeatureEnabled
import com.tminus1010.budgetvalue._unrestructured.transactions.view.TransactionListFrag
import com.tminus1010.budgetvalue.all_layers.extensions.onNext
import com.tminus1010.budgetvalue.all_layers.extensions.showAlertDialog
import com.tminus1010.budgetvalue.all_layers.extensions.unCheckAllMenuItems
import com.tminus1010.budgetvalue.app.ActivePlanInteractor
import com.tminus1010.budgetvalue.app.AppInitInteractor
import com.tminus1010.budgetvalue.app.ImportTransactions
import com.tminus1010.budgetvalue.app.IsPlanFeatureEnabledUC
import com.tminus1010.budgetvalue.databinding.ActivityHostBinding
import com.tminus1010.budgetvalue.framework.view.ShowAlertDialog
import com.tminus1010.budgetvalue.framework.view.SpinnerService
import com.tminus1010.budgetvalue.ui.errors.Errors
import com.tminus1010.budgetvalue.ui.futures.FuturesFrag
import com.tminus1010.budgetvalue.ui.importZ.ImportVM
import com.tminus1010.tmcommonkotlin.coroutines.extensions.observe
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.view.NativeText
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.subjects.Subject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HostActivity : AppCompatActivity() {
    private val vb by lazy { ActivityHostBinding.inflate(layoutInflater) }
    private val hostVM by viewModels<HostVM>()
    private val importVM by viewModels<ImportVM>()

    @Inject
    lateinit var appInitInteractor: AppInitInteractor

    @Inject
    lateinit var isPlanFeatureEnabledUC: IsPlanFeatureEnabledUC

    @Inject
    lateinit var isReconciliationFeatureEnabled: IsReconciliationFeatureEnabled

    @Inject
    lateinit var activePlanInteractor: ActivePlanInteractor

    @Inject
    lateinit var importTransactions: ImportTransactions

    @Inject
    lateinit var launchChooseFile: LaunchChooseFile

    @Inject
    lateinit var spinnerService: SpinnerService

    @Inject
    lateinit var errorSubject: Subject<Throwable>

    @Inject
    lateinit var errors: Errors

    val hostFrag by lazy { supportFragmentManager.findFragmentById(R.id.frag_nav_host) as HostFrag }
    private val nav by lazy { findNavController(R.id.frag_nav_host) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(vb.root)
        // # Mediation
        errorSubject.subscribe { errors.onNext(it) }
        hostVM.showAlertDialog.onNext(showAlertDialog)
        // # Initialize app once per install
        GlobalScope.launch { appInitInteractor.tryInitializeApp() }
        // # Bind bottom menu to navigation.
        // In order for NavigationUI.setupWithNavController to work, the ids in R.menu.* must exactly match R.navigation.*
        NavigationUI.setupWithNavController(vb.bottomNavigation, hostFrag.navController)
        //
        vb.bottomNavigation.selectedItemId = R.id.reviewFrag
        // # Events
        importVM.navToSelectFile.observe(this) { launchChooseFile(this) }
        isPlanFeatureEnabledUC.onChangeToTrue.observe(this) { activePlanInteractor.setActivePlanFromHistory(); showAlertDialog(hostVM.levelUpPlan) }
        isReconciliationFeatureEnabled.onChangeToTrue.observe(this) { showAlertDialog(hostVM.levelUpReconciliation) }
        hostVM.navToFutures.observe(this) { FuturesFrag.navTo(nav) }
        hostVM.navToTransactions.observe(this) { TransactionListFrag.navTo(nav) }
        hostVM.navToHistory.observe(this) { HistoryFrag.navTo(nav) }
        hostVM.navToAccessibility.observe(this) { startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }
        hostVM.unCheckAllMenuItems.observe(this) { vb.bottomNavigation.menu.unCheckAllMenuItems() } // TODO: Not working
        // # State
        isPlanFeatureEnabledUC.observe(this) { vb.bottomNavigation.menu.findItem(R.id.planFrag).isVisible = it }
        isReconciliationFeatureEnabled.observe(this) { vb.bottomNavigation.menu.findItem(R.id.reconciliationHostFrag).isVisible = it }
        spinnerService.isSpinnerVisible.observe(this) { vb.frameProgressBar.visibility = if (it) View.VISIBLE else View.GONE }
    }

    override fun onStart() {
        super.onStart()
        nav.addOnDestinationChangedListener { _, navDestination, _ -> Log.d("budgetvalue.Nav", "${navDestination.label}") }
        // # Setup VM
        hostVM.nav.onNext(nav)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        hostVM.topMenuVMItems.bind(menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }

    val importTransactionsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                try {
                    GlobalScope.launch {
                        val importTransactionsResult = importTransactions(result.data!!.data!!)
                        showAlertDialog(
                            NativeText.Simple(
                                """
                                    Import Successful
                                    ${importTransactionsResult.numberOfTransactionsImported} imported
                                    ${importTransactionsResult.numberOfTransactionsIgnoredBecauseTheyWereAlreadyImported} ignored because they were already imported
                                    ${importTransactionsResult.numberOfTransactionsCategorizedByFutures} categorized by futures
                                """.trimIndent()
                            )
                        )
                    }
                } catch (e: Throwable) {
                    logz("Error during importTransactions:", e)
                    hostFrag.handle(e)
                }
            }
        }
}
