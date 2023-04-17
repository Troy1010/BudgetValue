package com.tminus1010.buva.ui.host

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.tminus1010.buva.R
import com.tminus1010.buva.all_layers.extensions.onNext
import com.tminus1010.buva.app.IsReadyToBudget
import com.tminus1010.buva.app.get
import com.tminus1010.buva.data.SelectedHostPage
import com.tminus1010.buva.environment.android_wrapper.ActivityWrapper
import com.tminus1010.buva.ui.all_features.Navigator
import com.tminus1010.buva.ui.all_features.ReadyToBudgetPresentationService
import com.tminus1010.buva.ui.all_features.ThrobberSharedVM
import com.tminus1010.buva.ui.all_features.view_model_item.MenuVMItem
import com.tminus1010.buva.ui.all_features.view_model_item.MenuVMItems
import com.tminus1010.tmcommonkotlin.coroutines.extensions.observe
import com.tminus1010.tmcommonkotlin.coroutines.extensions.use
import com.tminus1010.tmcommonkotlin.view.NativeText
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HostVM @Inject constructor(
    getExtraMenuItemPartials: GetExtraMenuItemPartials,
    private val selectedHostPage: SelectedHostPage,
    private val navigator: Navigator,
    private val readyToBudgetPresentationService: ReadyToBudgetPresentationService,
    private val isReadyToBudget: IsReadyToBudget,
    private val activityWrapper: ActivityWrapper,
    private val throbberSharedVM: ThrobberSharedVM,
) : ViewModel() {
    // # Setup
    val nav = BehaviorSubject.create<NavController>()

    // # User Intents
    fun selectMenuItem(id: Int) {
        when (id) {
            R.id.budgetHostFrag ->
                GlobalScope.launch {
                    if (isReadyToBudget.get())
                        selectedHostPage.set(id)
                    else
                        GlobalScope.launch {
                            readyToBudgetPresentationService.tryShowAlertDialog(onContinue = { selectedHostPage.set(id) })
                        }
                }.use(throbberSharedVM)
            else ->
                selectedHostPage.set(id)
        }
    }

    fun userTryNavToAccessibilitySettings() {
        GlobalScope.launch {
            activityWrapper.showAlertDialog(
                body = NativeText.Simple(
                    """
                    Accessibility settings apply to all applications, so you must edit them in your phone's settings.
                    
                    Would you like to go there now?
                    """.trimIndent()
                ),
                onYes = navToAccessibility::onNext)
        }
    }

    // # Private
    init {
        selectedHostPage.flow.observe(viewModelScope) { navigator.navTo(it) }
    }

    // # Events
    val unCheckAllMenuItems = MutableSharedFlow<Unit>()
    val navToAccessibility = MutableSharedFlow<Unit>()

    // # State
    val topMenuVMItems =
        MenuVMItems(
            MenuVMItem(
                title = "History",
                onClick = { navigator.navToHistory(); unCheckAllMenuItems.onNext() },
            ),
            MenuVMItem(
                title = "Transactions",
                onClick = { navigator.navToTransactions(); unCheckAllMenuItems.onNext() },
            ),
            MenuVMItem(
                title = "Futures",
                onClick = { navigator.navToFutures(); unCheckAllMenuItems.onNext() },
            ),
            MenuVMItem(
                title = "Accessibility Settings",
                onClick = { userTryNavToAccessibilitySettings() },
            ),
            *getExtraMenuItemPartials(nav)
        )

    // TODO: Cleanup? Unless I want to work on this feature..
    val levelUpPlan = NativeText.Multi(NativeText.Resource(R.string.level_up_prefix), NativeText.Simple(" "), NativeText.Resource(R.string.level_up_plan))

    // TODO: Cleanup? Unless I want to work on this feature..
    val levelUpReconciliation = NativeText.Multi(NativeText.Resource(R.string.level_up_prefix), NativeText.Simple(" "), NativeText.Resource(R.string.level_up_reconciliation))
}