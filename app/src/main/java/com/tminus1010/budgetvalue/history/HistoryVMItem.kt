package com.tminus1010.budgetvalue.history

import com.tminus1010.tmcommonkotlin.tuple.Box
import io.reactivex.rxjava3.core.Observable

class HistoryVMItem {
    val title: String = TODO()
    val subTitle: Observable<Box<String?>> = TODO()
    val defaultAmount: Observable<String> = TODO()
    val categoryAmounts: AmountsVMItem = TODO()
}