package com.tminus1010.budgetvalue.ui.host

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.ui.history.HistoryFrag
import com.tminus1010.budgetvalue._unrestructured.transactions.view.TransactionListFrag
import com.tminus1010.budgetvalue.all_layers.extensions.onNext
import com.tminus1010.budgetvalue.all_layers.extensions.showAlertDialog
import com.tminus1010.budgetvalue.app.*
import com.tminus1010.budgetvalue.databinding.ActivityHostBinding
import com.tminus1010.budgetvalue.ui.all_features.ThrobberSharedVM
import com.tminus1010.budgetvalue.ui.futures.FuturesFrag
import com.tminus1010.budgetvalue.ui.importZ.ImportSharedVM
import com.tminus1010.tmcommonkotlin.coroutines.extensions.observe
import com.tminus1010.tmcommonkotlin.coroutines.extensions.pairwise
import com.tminus1010.tmcommonkotlin.view.NativeText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HostActivity : AppCompatActivity() {
    private val vb by lazy { ActivityHostBinding.inflate(layoutInflater) }
    private val viewModel by viewModels<HostVM>()

    @Inject
    lateinit var tryInitApp: TryInitApp

    @Inject
    lateinit var isPlanFeatureEnabled: IsPlanFeatureEnabled

    @Inject
    lateinit var isReconciliationFeatureEnabled: IsReconciliationFeatureEnabled

    @Inject
    lateinit var activePlanInteractor: ActivePlanInteractor

    @Inject
    lateinit var importTransactions: ImportTransactions

    @Inject
    lateinit var launchChooseFile: LaunchChooseFile

    @Inject
    lateinit var throbberSharedVM: ThrobberSharedVM

    @Inject
    lateinit var importSharedVM: ImportSharedVM

    val hostFrag by lazy { supportFragmentManager.findFragmentById(R.id.frag_nav_host) as HostFrag }
    private val nav by lazy { findNavController(R.id.frag_nav_host) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(vb.root)
        // # Mediation
        viewModel.showAlertDialog.onNext(showAlertDialog)
        // # Initialize app once per install
        GlobalScope.launch { tryInitApp() }
        // # Bind bottom menu to navigation.
        // In order for NavigationUI.setupWithNavController to work, the ids in R.menu.* must exactly match R.navigation.*
        NavigationUI.setupWithNavController(vb.bottomNavigation, hostFrag.navController)
        //
        vb.bottomNavigation.selectedItemId = R.id.reviewFrag
        // # Events
        importSharedVM.navToSelectFile.observe(this) { launchChooseFile(this) }
        isPlanFeatureEnabled.flow.pairwise().filter { !it.first && it.second }.observe(this) { activePlanInteractor.setActivePlanFromHistory(); showAlertDialog(viewModel.levelUpPlan) }
        isReconciliationFeatureEnabled.flow.pairwise().filter { !it.first && it.second }.observe(this) { showAlertDialog(viewModel.levelUpReconciliation) }
        viewModel.navToFutures.observe(this) { FuturesFrag.navTo(nav) }
        viewModel.navToTransactions.observe(this) { TransactionListFrag.navTo(nav) }
        viewModel.navToHistory.observe(this) { HistoryFrag.navTo(nav) }
        viewModel.navToAccessibility.observe(this) { startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }
        viewModel.unCheckAllMenuItems.observe(this) { vb.bottomNavigation.menu.unCheckAllMenuItems() } // TODO: Not working
        // # State
        isPlanFeatureEnabled.flow.observe(this) { vb.bottomNavigation.menu.findItem(R.id.planFrag).isVisible = it }
        isReconciliationFeatureEnabled.flow.observe(this) { vb.bottomNavigation.menu.findItem(R.id.reconciliationHostFrag).isVisible = it }
        vb.frameProgressBar.bind(throbberSharedVM.isVisible) { easyVisibility = it }
    }

    override fun onStart() {
        super.onStart()
        nav.addOnDestinationChangedListener { _, navDestination, _ -> Log.d("budgetvalue.Nav", "${navDestination.label}") }
        // # Setup VM
        viewModel.nav.onNext(nav)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        viewModel.topMenuVMItems.bind(menu)
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
