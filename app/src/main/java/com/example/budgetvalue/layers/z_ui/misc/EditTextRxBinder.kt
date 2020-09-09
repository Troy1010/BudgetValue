package com.example.budgetvalue.layers.z_ui.misc

import android.widget.EditText
import io.reactivex.rxjava3.subjects.BehaviorSubject

class EditTextRxBinder<T>(
    val validate: (T)->T,
    val toT:(String)->T,
    val toDisplayStr:((T)->String)? = null
) {
    fun rxBind(v: EditText, bs:BehaviorSubject<T>) {
        if (toDisplayStr!=null) {
            v.rxBind(bs, validate, toT, toDisplayStr)
        } else {
            v.rxBind(bs, validate, toT)
        }
    }
}