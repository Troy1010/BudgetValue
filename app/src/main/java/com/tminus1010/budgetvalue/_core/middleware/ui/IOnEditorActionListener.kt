package com.tminus1010.budgetvalue._core.middleware.ui

import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import io.reactivex.rxjava3.core.Observable

interface IOnEditorActionListener {
    val onEditorActionListener: Observable<Triple<TextView, Int, KeyEvent>>
}