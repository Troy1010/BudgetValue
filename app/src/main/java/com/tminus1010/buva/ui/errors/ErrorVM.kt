package com.tminus1010.buva.ui.errors

import androidx.lifecycle.ViewModel
import com.tminus1010.buva.ui.all_features.view_model_item.ButtonVMItem
import io.reactivex.rxjava3.subjects.BehaviorSubject

class ErrorVM: ViewModel() {
    val message = BehaviorSubject.createDefault("")
    val buttons = BehaviorSubject.createDefault(emptyList<ButtonVMItem>())
}