package com.tminus1010.buva.ui.host

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.tminus1010.buva.R
import com.tminus1010.buva.all_layers.extensions.onNext
import com.tminus1010.buva.all_layers.extensions.value
import com.tminus1010.buva.data.SelectedPage
import com.tminus1010.buva.ui.all_features.Navigator
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
    private val selectedPage: SelectedPage,
    private val navigator: Navigator,
) : ViewModel() {
    // # Setup
    val nav = BehaviorSubject.create<NavController>()
    val showAlertDialog = MutableSharedFlow<ShowAlertDialog>(1)

    // # User Intents
    fun selectMenuItem(int: Int) {
        selectedPage.set(int)
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
    val selectedPageRedefined = selectedPage.flow
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
            MenuVMItem(
                title = "Old Import",
                onClick = { navigator.navToImport(); unCheckAllMenuItems.onNext() },
            ),
            MenuVMItem(
                title = "Old Categorize",
                onClick = { navigator.navToCategorize(); unCheckAllMenuItems.onNext() },
            ),
            *getExtraMenuItemPartials(nav)
        )
    val levelUpPlan = NativeText.Multi(NativeText.Resource(R.string.level_up_prefix), NativeText.Simple(" "), NativeText.Resource(R.string.level_up_plan))
    val levelUpReconciliation = NativeText.Multi(NativeText.Resource(R.string.level_up_prefix), NativeText.Simple(" "), NativeText.Resource(R.string.level_up_reconciliation))
}