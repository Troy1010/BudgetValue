package com.tminus1010.budgetvalue.all.presentation_and_view.reconciliation_host

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._core.middleware.presentation.ButtonVMItem
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

@HiltViewModel
class ReconciliationHostVM @Inject constructor(
) : ViewModel() {
    // # Presentation Output
    val buttons =
        Observable.just(
            listOf(
                ButtonVMItem(
                    title = "Account Reconciliation",
                    onClick = { TODO() }
                )
            )
        )!!

    val title =
        Observable.just(
            "No Reconciliations Required" // TODO()
        )!!
}