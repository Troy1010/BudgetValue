package com.tminus1010.budgetvalue.layer_ui

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.extensions.unbox
import com.tminus1010.budgetvalue.model_app.Category
import com.tminus1010.budgetvalue.unbox
import com.tminus1010.tmcommonkotlin_rx.toBehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.math.BigDecimal

class AdvancedCategorizeVM(categorizeVM: CategorizeVM) : ViewModel() {
    val intentRememberCA = PublishSubject.create<Pair<Category, BigDecimal>>()
    val defaultAmount = categorizeVM.transactionBox
        .map { it.unbox?.defaultAmount!! }
        .toBehaviorSubject()
    val transactionToPush = categorizeVM.transactionBox.unbox()
        .switchMap {
            intentRememberCA
                .scan(it) { acc, v ->
                    acc.copy(categoryAmounts = acc.categoryAmounts.toMutableMap().also { it[v.first] = v.second })
                }
        }
}