package com.example.budgetvalue.layers.z_ui.misc

import android.view.View
import android.widget.EditText
import android.widget.TextView
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.math.BigDecimal

fun <T> EditText.rxBind(bs: BehaviorSubject<T>, editTextRxBinder:EditTextRxBinder<T>) {
    editTextRxBinder.rxBind(this, bs)
}

fun EditText.rxBind(bs:BehaviorSubject<String?>, validate: (String?)->String = { it?:"" }): Disposable {
    return this.rxBind(bs, validate, { it }, { it?:"" } )
}

fun <T> EditText.rxBind(
    bs:BehaviorSubject<T>,
    validate: (T)->T,
    toT:(String)->T,
    toDisplayStr:(T)->String = { it.toString() }): Disposable {
    val rxDisposable = bs.distinctUntilChanged().subscribe {
        if (layoutParams!=null) {
            this.setText(toDisplayStr(it))
        }
    }
    this.onFocusChangeListener = View.OnFocusChangeListener { _, isFocused ->
        if (!isFocused) {
            val mText = this.text.toString()
            val mTextValidated = toDisplayStr(validate(toT(mText)))
            // validate
            if (mText != mTextValidated) {
                this.setText(mTextValidated)
            }
            // emit
            if (this.text.toString() != bs.value) {
                bs.onNext(validate(toT(this.text.toString())))
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


fun <T> TextView.rxBindOneWay(
    observable: Observable<T>,
    toDisplayStr:(T)->String = { it.toString() }
): Disposable {
    return observable.distinctUntilChanged().subscribe { value ->
        layoutParams?.let { setText(toDisplayStr(value)) }
    }
}



fun Iterable<BigDecimal>.sum(): BigDecimal {
    return this.fold(BigDecimal.ZERO) { accumulator, value -> accumulator + value }
}