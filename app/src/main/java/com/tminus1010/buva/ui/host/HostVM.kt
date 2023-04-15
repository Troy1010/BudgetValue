package com.tminus1010.buva.ui.host

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.tminus1010.buva.R
import com.tminus1010.buva.all_layers.extensions.onNext
import com.tminus1010.buva.all_layers.extensions.value
import com.tminus1010.buva.data.SelectedBudgetHostPage
import com.tminus1010.buva.data.SelectedHostPage
import com.tminus1010.buva.ui.all_features.Navigator
import com.tminus1010.buva.ui.all_features.ReadyToBudgetPresentationFactory
import com.tminus1010.buva.ui.all_features.ReadyToReconcilePresentationService
import com.tminus1010.buva.ui.all_features.view_model_item.MenuVMItem
import com.tminus1010.buva.ui.all_features.view_model_item.MenuVMItems
import com.tminus1010.tmcommonkotlin.androidx.ShowAlertDialog
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
    private val readyToBudgetPresentationFactory: ReadyToBudgetPresentationFactory,
    private val readyToReconcilePresentationService: ReadyToReconcilePresentationService,
    private val selectedBudgetHostPage: SelectedBudgetHostPage,
) : ViewModel() {
    // # Setup
    val nav = BehaviorSubject.create<NavController>()
    val showAlertDialog = MutableSharedFlow<ShowAlertDialog>(1)

    // # User Intents
    fun selectMenuItem(id: Int) {
        // Requirement: Given app is not readyToBudget When user navigates to BudgetHost Then show popup.
        selectedHostPage.set(id)
    }

    fun userTryNavToAccessibilitySettings() {
        GlobalScope.launch {
            showAlertDialog.value!!(
                NativeText.Simple(
                    """
                    Accessibility settings apply to all applications, so you must edit them in your phone's settings.
                    
                    Would you like to go there now?
                    """.trimIndent()
                ),
                onYes = navToAccessibility::onNext)
        }
    }

    // # Events
    val unCheckAllMenuItems = MutableSharedFlow<Unit>()
    val navToAccessibility = MutableSharedFlow<Unit>()

    // # State
    val selectedItemId = selectedHostPage.flow
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