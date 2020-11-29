package com.example.budgetvalue.layer_ui.misc

import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.Subject

fun <T> EditText.rxBind(bs: BehaviorSubject<T>, editTextRxBinder:EditTextRxBinder<T>) {
    editTextRxBinder.rxBind(this, bs)
}

fun EditText.rxBind(bs:BehaviorSubject<String?>, validate: (String?)->String = { it?:"" }): Disposable {
    return this.rxBind(bs, { it }, validate, { it?:"" } )
}

fun <T> EditText.rxBind(
    bs:BehaviorSubject<T>,
    toT:(String)->T,
    validate: (T)->T = { it }, // TODO could be more performant
    toDisplayStr:(T)->String = { it.toString() }): Disposable {
    val rxDisposable = bindIncoming(bs)
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

fun <T> TextView.bindDownstream(
    observable: Observable<T>,
    provideDisplayable:((T)->Any)? = null
): Disposable {
    return observable
        .distinctUntilChanged()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe {
            if (this.layoutParams!=null)
                this.text = (provideDisplayable?.invoke(it) ?: it)
                    .toString()
        }
}

fun <T:Any> TextView.bindIncoming(
    observable: Observable<T>,
    toDisplayable:((T)->Any)? = null
): Disposable {
    return observable
        .distinctUntilChanged()
        .observeOn(AndroidSchedulers.mainThread())
        .filter { this.layoutParams!=null }
        .subscribe {
            this.text = it
                .let { if (toDisplayable==null) it else toDisplayable(it) }
                .toString()
        }
}

fun Button.setOnClickListener(subject: Subject<Unit>) {
    setOnClickListener { subject.onNext(Unit) }
}