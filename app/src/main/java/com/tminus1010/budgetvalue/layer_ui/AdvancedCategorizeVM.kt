package com.tminus1010.budgetvalue.layer_ui

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.model_app.Category
import com.tminus1010.tmcommonkotlin_rx.toBehaviorSubject
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.math.BigDecimal

class AdvancedCategorizeVM(private val categoriesAppVM: CategoriesAppVM) : ViewModel() {
    val intentRememberCA = PublishSubject.create<Pair<Category, BigDecimal>>()
    var intentRememberAmount = BehaviorSubject.createDefault(BigDecimal.ZERO)
    val categoryAmounts = categoriesAppVM.choosableCategories
        .map { it.associateWith { BigDecimal.ZERO } }
        .toBehaviorSubject()
}