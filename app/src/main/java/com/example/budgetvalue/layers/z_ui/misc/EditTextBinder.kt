package com.example.budgetvalue.layers.z_ui.misc

import android.widget.EditText
import io.reactivex.rxjava3.subjects.BehaviorSubject

class EditTextBinder<T>(
    val validate: (T)->T,
    val toDisplayStr:(T)->String,
    val toT:(String)->T
) {
    fun bind(v: EditText, bs:BehaviorSubject<T>) {
        v.bind(bs, validate, toDisplayStr, toT)
    }
}