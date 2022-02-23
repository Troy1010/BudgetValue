package com.tminus1010.budgetvalue._core.presentation.view_model

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.presentation.model.MenuPresentationModel
import com.tminus1010.budgetvalue._core.presentation.model.MenuVMItem
import com.tminus1010.budgetvalue._core.presentation.model.UnformattedString
import com.tminus1010.budgetvalue._core.presentation.service.GetExtraMenuItemPartials
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import javax.inject.Inject

@HiltViewModel
class HostVM @Inject constructor(
    getExtraMenuItemPartials: GetExtraMenuItemPartials,
) : ViewModel() {
    // # Setup
    val nav = BehaviorSubject.create<NavController>()

    // # Events
    val unCheckAllMenuItems = PublishSubject.create<Unit>()
    val navToHistory = PublishSubject.create<Unit>()
    val navToTransactions = PublishSubject.create<Unit>()
    val navToFutures = PublishSubject.create<Unit>()
    val navToReplays = PublishSubject.create<Unit>()

    // # State
    val topMenuVMItems =
        MenuPresentationModel(
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
                title = "Replays",
                onClick = { navToReplays.onNext(Unit); unCheckAllMenuItems.onNext(Unit) },
            ),
            *getExtraMenuItemPartials(nav)
        )
    val levelUpPlan = UnformattedString(R.string.level_up_prefix, " ", R.string.level_up_plan)
    val levelUpReconciliation = UnformattedString(R.string.level_up_prefix, " ", R.string.level_up_reconciliation)
}