package com.tminus1010.budgetvalue.ui.host

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.all_layers.extensions.onNext
import com.tminus1010.budgetvalue.ui.all_features.model.MenuVMItem
import com.tminus1010.budgetvalue.ui.all_features.model.MenuVMItems
import com.tminus1010.tmcommonkotlin.view.NativeText
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject

@HiltViewModel
class HostVM @Inject constructor(
    getExtraMenuItemPartials: GetExtraMenuItemPartials,
) : ViewModel() {
    // # Setup
    val nav = BehaviorSubject.create<NavController>()

    // # Events
    val unCheckAllMenuItems = MutableSharedFlow<Unit>()
    val navToHistory = MutableSharedFlow<Unit>()
    val navToTransactions = MutableSharedFlow<Unit>()
    val navToFutures = MutableSharedFlow<Unit>()
    val navToAccessibility = MutableSharedFlow<Unit>()

    // # State
    val topMenuVMItems =
        MenuVMItems(
            MenuVMItem(
                title = "History",
                onClick = { navToHistory.onNext(Unit); unCheckAllMenuItems.onNext(Unit) },
            ),
            MenuVMItem(
                title = "Transactions",
                onClick = { navToTransactions.onNext(Unit); unCheckAllMenuItems.onNext(Unit) },
            ),
            MenuVMItem(
                title = "Futures",
                onClick = { navToFutures.onNext(Unit); unCheckAllMenuItems.onNext(Unit) },
            ),
            MenuVMItem(
                title = "Accessibility Settings",
                onClick = { navToFutures.onNext(Unit); unCheckAllMenuItems.onNext(Unit) },
            ),
            *getExtraMenuItemPartials(nav)
        )
    val levelUpPlan = NativeText.Multi(NativeText.Resource(R.string.level_up_prefix), NativeText.Simple(" "), NativeText.Resource(R.string.level_up_plan))
    val levelUpReconciliation = NativeText.Multi(NativeText.Resource(R.string.level_up_prefix), NativeText.Simple(" "), NativeText.Resource(R.string.level_up_reconciliation))
}