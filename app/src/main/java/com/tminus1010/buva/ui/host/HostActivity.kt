package com.tminus1010.buva.ui.host

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.tminus1010.buva.R
import com.tminus1010.buva.all_layers.extensions.onNext
import com.tminus1010.buva.all_layers.extensions.unCheckAllItems
import com.tminus1010.buva.all_layers.extensions.value
import com.tminus1010.buva.app.*
import com.tminus1010.buva.databinding.ActivityHostBinding
import com.tminus1010.buva.environment.ActivityWrapper
import com.tminus1010.buva.environment.AndroidNavigationWrapperImpl
import com.tminus1010.buva.environment.HostActivityWrapper
import com.tminus1010.buva.ui.all_features.ShowImportResultAlertDialog
import com.tminus1010.buva.ui.all_features.ThrobberSharedVM
import com.tminus1010.tmcommonkotlin.androidx.ShowAlertDialog
import com.tminus1010.tmcommonkotlin.androidx.ShowToast
import com.tminus1010.tmcommonkotlin.core.tryOrNull
import com.tminus1010.tmcommonkotlin.coroutines.extensions.observe
import com.tminus1010.tmcommonkotlin.coroutines.extensions.pairwise
import com.tminus1010.tmcommonkotlin.coroutines.extensions.use
import com.tminus1010.tmcommonkotlin.customviews.extensions.bind
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


@AndroidEntryPoint
class HostActivity : AppCompatActivity() {
    private val vb by lazy { ActivityHostBinding.inflate(layoutInflater) }
    private val viewModel by viewModels<HostVM>()

    @Inject
    lateinit var initApp: InitApp

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
    lateinit var transactionsInteractor: TransactionsInteractor

    @Inject
    lateinit var showToast: ShowToast

    val hostFrag by lazy { supportFragmentManager.findFragmentById(R.id.fragmentcontainerview) as HostFrag }
    private val nav by lazy { findNavController(R.id.fragmentcontainerview) }
    private val showImportResultAlertDialog by lazy { ShowImportResultAlertDialog(ShowAlertDialog(this)) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(vb.root)
        // # Setup
        // ## Logs
        hostFrag.navController.addOnDestinationChangedListener { _, navDestination, _ -> Log.d("buva.Nav", "${navDestination.label}") }
        // ## Mediation
        ActivityWrapper.activity = this
        HostActivityWrapper.hostActivity = this
        AndroidNavigationWrapperImpl.nav = hostFrag.navController
        viewModel.showAlertDialog.onNext(ShowAlertDialog(this))
        // ## Initialize app once per install
        GlobalScope.launch { initApp() }.use(throbberSharedVM)
        // ## Bind bottom menu to navigation.
        // In order for NavigationUI.setupWithNavController to work, the ids in R.menu.* must exactly match R.navigation.*
        NavigationUI.setupWithNavController(vb.bottomnavigationview, hostFrag.navController)
        //
        vb.bottomnavigationview.setOnItemSelectedListener {
            // Requirement: When config change Then do not forget current menu item.
            viewModel.selectMenuItem(it.itemId)
            // Requirement: Given some spends are not categorized When Reconciliation is clicked Then show toast.
            if (
                it.itemId == R.id.reconciliationHostFrag
                // TODO: Blocking is not ideal.
                && !runBlocking { transactionsInteractor.transactionsAggregate.first() }.areAllSpendsCategorized
            ) {
                showToast("Can't reconcile until all spends are categorized.")
                return@setOnItemSelectedListener false
            }
            // Requirement: When menu item clicked Then forget backstack.
            // This will be null When config change.
            val nav = tryOrNull { findNavController(R.id.fragmentcontainerview) }
            // clearBackStack might not be necessary.
            nav?.clearBackStack(it.itemId)
            nav?.navigate(it.itemId)
            // setOnItemSelectedListener overrides setupWithNavController's behavior, so that behavior is restored here.
            NavigationUI.onNavDestinationSelected(it, hostFrag.navController)
        }
        viewModel.selectedPageRedefined.value?.also { vb.bottomnavigationview.selectedItemId = it }
        // # Events
        isPlanFeatureEnabled.flow.pairwise().filter { !it.first && it.second }.observe(this) { activePlanInteractor.estimateActivePlanFromHistory(); ShowAlertDialog(this)(viewModel.levelUpPlan) }
        isReconciliationFeatureEnabled.flow.pairwise().filter { !it.first && it.second }.observe(this) { ShowAlertDialog(this)(viewModel.levelUpReconciliation) }
        viewModel.navToAccessibility.observe(this) { startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }
        viewModel.unCheckAllMenuItems.observe(this) { vb.bottomnavigationview.unCheckAllItems() } // TODO: Not working
        // # State
        isPlanFeatureEnabled.flow.observe(this) { vb.bottomnavigationview.menu.findItem(R.id.planFrag).isVisible = it }
        isReconciliationFeatureEnabled.flow.observe(this) { vb.bottomnavigationview.menu.findItem(R.id.reconciliationHostFrag).isVisible = it }
        vb.frameProgressBar.bind(throbberSharedVM.visibility) { visibility = it }
    }

    override fun onStart() {
        super.onStart()
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

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v: View? = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    val coroutineExceptionHandler =
        CoroutineExceptionHandler { _, e ->
            logz("Error during importTransactions:", e)
            hostFrag.handle(e)
        }

    val importTransactionsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK)
                GlobalScope.launch(coroutineExceptionHandler) {
                    showImportResultAlertDialog(importTransactions(result.data!!.data!!))
                }.use(throbberSharedVM)
        }
}
