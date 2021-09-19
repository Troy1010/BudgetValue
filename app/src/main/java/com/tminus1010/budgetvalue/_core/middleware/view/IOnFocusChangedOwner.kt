package com.tminus1010.budgetvalue._core.middleware.view

import android.view.View
import io.reactivex.rxjava3.core.Observable

interface IOnFocusChangedOwner {
    val onFocusChanged: Observable<Pair<View, Boolean>>
}