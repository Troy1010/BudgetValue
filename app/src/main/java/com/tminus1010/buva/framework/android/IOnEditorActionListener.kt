package com.tminus1010.buva.framework.android

import android.view.KeyEvent
import android.widget.TextView
import io.reactivex.rxjava3.core.Observable

interface IOnEditorActionListener {
    val onEditorActionListener: Observable<Triple<TextView, Int, KeyEvent>>
}