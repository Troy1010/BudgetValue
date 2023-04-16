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
import androidx.core.view.forEach
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.tminus1010.buva.R
import com.tminus1010.buva.all_layers.extensions.isSettingSelectedItemId
import com.tminus1010.buva.all_layers.extensions.items
import com.tminus1010.buva.all_layers.extensions.onNext
import com.tminus1010.buva.all_layers.extensions.unCheckAllItems
import com.tminus1010.buva.app.ActivePlanInteractor
import com.tminus1010.buva.app.ImportTransactions
import com.tminus1010.buva.app.InitApp
import com.tminus1010.buva.app.TransactionsInteractor
import com.tminus1010.buva.data.AccountsRepo
import com.tminus1010.buva.data.SelectedHostPage
import com.tminus1010.buva.data.TransactionsRepo
import com.tminus1010.buva.databinding.ActivityHostBinding
import com.tminus1010.buva.environment.ActivityWrapper
import com.tminus1010.buva.environment.AndroidNavigationWrapperImpl
import com.tminus1010.buva.environment.HostActivityWrapper
import com.tminus1010.buva.ui.all_features.ReadyToBudgetPresentationFactory
import com.tminus1010.buva.ui.all_features.ShowImportResultAlertDialog
import com.tminus1010.buva.ui.all_features.ThrobberSharedVM
import com.tminus1010.tmcommonkotlin.androidx.ShowAlertDialog
import com.tminus1010.tmcommonkotlin.androidx.ShowToast
import com.tminus1010.tmcommonkotlin.core.tryOrNull
import com.tminus1010.tmcommonkotlin.coroutines.extensions.observe
import com.tminus1010.tmcommonkotlin.coroutines.extensions.use
import com.tminus1010.tmcommonkotlin.customviews.extensions.bind
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import javax.inject.Inject


@AndroidEntryPoint
class HostActivity : AppCompatActivity() {
    private val vb by lazy { ActivityHostBinding.inflate(layoutInflater) }
    private val viewModel by viewModels<HostVM>()

    @Inject
    lateinit var initApp: InitApp

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
    lateinit var accountsRepo: AccountsRepo

    @Inject
    lateinit var showToast: ShowToast

    @Inject
    lateinit var activityWrapper: ActivityWrapper

    @Inject
    lateinit var transactionsRepo: TransactionsRepo

    @Inject
    lateinit var readyToBudgetPresentationFactory: ReadyToBudgetPresentationFactory

    @Inject
    lateinit var selectedHostPage: SelectedHostPage

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
        viewModel.showAlertDialog.onNext(ShowAlertDialog(this)) // TODO: Refactor
        // ## Initialize app TODO: Shouldn't this be in BaseApp?
        GlobalScope.launch { initApp() }.use(throbberSharedVM)
        // ## SetupWithNavController
        // In order for NavigationUI.setupWithNavController to work, the ids in R.menu.* must exactly match R.navigation.*
        // Even though we are overriding setOnItemSelectedListener, we still need this for addOnDestinationChangedListener so that if Navigator brings us here, the correct highlight is applied.
        // I tried to remove this and do my own addOnDestinationChangedListener, but unfortunately, the matchDestination method is blocked.
//        x()
        // hierarchy.any { it.id == destId }
        // # User Intent
        vb.bottomnavigationview.setOnItemSelectedListener {
            if (!vb.bottomnavigationview.isSettingSelectedItemId) {
                viewModel.selectMenuItem(it.itemId)
                false
            } else {
                true
            }
        }
        // # Events
        viewModel.navToAccessibility.observe(this) { startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }
        viewModel.unCheckAllMenuItems.observe(this) { vb.bottomnavigationview.unCheckAllItems() } // TODO: Not working
        viewModel.navToId.observe(this) { navToId(it) }
        // # State
        vb.bottomnavigationview.bind(viewModel.selectedItemId) { isSettingSelectedItemId = true; selectedItemId = it; isSettingSelectedItemId = false }
        vb.frameProgressBar.bind(throbberSharedVM.visibility) { visibility = it }
    }

//    fun x() {
//        val weakReference = WeakReference(vb.bottomnavigationview)
//        nav.addOnDestinationChangedListener(
//            object : NavController.OnDestinationChangedListener {
//                override fun onDestinationChanged(
//                    controller: NavController,
//                    destination: NavDestination,
//                    arguments: Bundle?,
//                ) {
//                    val view = weakReference.get()
//                    if (view == null) {
//                        nav.removeOnDestinationChangedListener(this)
//                        return
//                    }
//                    view.menu.forEach { item ->
//                        if (destination.hierarchy.any { it.id == item.itemId }) {
////                            item.isChecked = true
//                            selectedHostPage.set(item.itemId)
//                        }
//                    }
//                }
//            })
//    }

    fun navToId(id: Int): Boolean {
        // Requirement: When menu item clicked Then forget backstack.
        // This will be null When config change.
        val nav = tryOrNull { findNavController(R.id.fragmentcontainerview) }
        // clearBackStack might not be necessary.
        nav?.clearBackStack(id)
        nav?.navigate(id)
        // setOnItemSelectedListener overrides setupWithNavController's behavior, so that behavior is restored here.
        val menuItem = vb.bottomnavigationview.menu.items.find { it.itemId == id }!!
        return NavigationUI.onNavDestinationSelected(menuItem, hostFrag.navController)
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
