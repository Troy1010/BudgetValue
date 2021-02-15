package com.tminus1010.budgetvalue.layer_ui

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.model_app.Category
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.math.BigDecimal

class AdvancedCategorizeVM : ViewModel() {
    val intentRememberCA = PublishSubject.create<Pair<Category, BigDecimal>>()
    val intentRememberAmount = BehaviorSubject.createDefault(BigDecimal.ZERO)
}