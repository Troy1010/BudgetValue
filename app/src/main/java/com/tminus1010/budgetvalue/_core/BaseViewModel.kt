package com.tminus1010.budgetvalue._core

import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable

open class BaseViewModel: ViewModel() {
    val disposables = CompositeDisposable()
    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}