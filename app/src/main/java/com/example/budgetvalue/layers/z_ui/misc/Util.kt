package com.example.budgetvalue.layers.z_ui.misc

import android.view.View
import android.widget.EditText
import androidx.annotation.NonNull
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.BehaviorSubject

fun <T> EditText.bind(
    bs:BehaviorSubject<T>,
    validate: (T)->T,
    display:(T)->String,
    convertBack:(String)->T): Disposable {
    val rxDisposable = bs.distinctUntilChanged().subscribe {
        if (layoutParams!=null) {
            this.setText(display(it))
        }
    }
    this.onFocusChangeListener = View.OnFocusChangeListener { _, isFocused ->
        if (!isFocused) {
            val mText = this.text.toString()
            val mTextValidated = display(validate(convertBack(mText)))
            // validate
            if (mText != mTextValidated) {
                this.setText(mTextValidated)
            }
            // emit
            if (this.text.toString() != bs.value) {
                bs.onNext(validate(convertBack(this.text.toString())))
            }
        }
    }
    return object : Disposable {
        var bDisposed = false
        override fun dispose() {
            if (!isDisposed) {
                bDisposed = true
                rxDisposable.dispose()
                onFocusChangeListener = null
            }
        }
        override fun isDisposed() = bDisposed
    }
}

fun EditText.bind(bs:BehaviorSubject<String?>, validate: (String?)->String = { it?:"" }): Disposable {
    return this.bind(bs, validate, { it?:"" }, { it })
}