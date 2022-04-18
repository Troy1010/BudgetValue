package com.tminus1010.buva.ui.host

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.tminus1010.buva.R
import com.tminus1010.buva.all_layers.extensions.onNext
import com.tminus1010.buva.all_layers.extensions.value
import com.tminus1010.buva.data.SelectedPage
import com.tminus1010.buva.framework.android.ShowAlertDialog
import com.tminus1010.buva.ui.all_features.view_model_item.MenuVMItem
import com.tminus1010.buva.ui.all_features.view_model_item.MenuVMItems
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
) : ViewModel() {
    // # Setup
    val nav = BehaviorSubject.create<NavController>()
    val showAlertDialog = MutableSharedFlow<ShowAlertDialog>(1)

    // # User Intents
    fun selectMenuItem(int: Int) {
        selectedPage.set(int)
    }

    // # Events
    val unCheckAllMenuItems = MutableSharedFlow<Unit>()
    val navToHistory = MutableSharedFlow<Unit>()
    val navToTransactions = MutableSharedFlow<Unit>()
    val navToFutures = MutableSharedFlow<Unit>()
    val navToAccessibility = MutableSharedFlow<Unit>()

    // # State
    val selectedPageRedefined = selectedPage.flow
    val topMenuVMItems =
        MenuVMItems(
            MenuVMItem(
                title = "History",
                onClick = { navToHistory.onNext(); unCheckAllMenuItems.onNext() },
            ),
            MenuVMItem(
                title = "Transactions",
                onClick = { navToTransactions.onNext(); unCheckAllMenuItems.onNext() },
            ),
            MenuVMItem(
                title = "Futures",
                onClick = { navToFutures.onNext(); unCheckAllMenuItems.onNext() },
            ),
            MenuVMItem(
                title = "Accessibility Settings",
                onClick = {
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
                },
            ),
            *getExtraMenuItemPartials(nav)
        )
    val levelUpPlan = NativeText.Multi(NativeText.Resource(R.string.level_up_prefix), NativeText.Simple(" "), NativeText.Resource(R.string.level_up_plan))
    val levelUpReconciliation = NativeText.Multi(NativeText.Resource(R.string.level_up_prefix), NativeText.Simple(" "), NativeText.Resource(R.string.level_up_reconciliation))
}