package com.example.budgetvalue.layers.z_ui.misc

import android.widget.EditText
import io.reactivex.rxjava3.subjects.BehaviorSubject

class EditTextBinder<T>(
    val validate: (T)->T,
    val toT:(String)->T,
    val toDisplayStr:((T)->String)? = null
) {
    fun bind(v: EditText, bs:BehaviorSubject<T>) {
        if (toDisplayStr!=null) {
            v.bind(bs, validate, toT, toDisplayStr)
        } else {
            v.bind(bs, validate, toT)
        }
    }
}