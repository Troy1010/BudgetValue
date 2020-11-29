package com.example.budgetvalue.layer_ui.misc

import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.jakewharton.rxbinding4.view.focusChanges
import com.jakewharton.rxbinding4.widget.textChanges
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.Subject

// * I'm not sure if this is the best idea. It might be better to have 2 different incoming and outgoing streams.
fun <T> EditText.bind(
    subject:Subject<T>,
    toT:(String)->T,
    validate: ((T)->T)? = null,
    toDisplayable:((T)->Any)? = null
) {
    bindIncoming(subject, toDisplayable)
    bindOutgoing(subject, toT, validate, toDisplayable)
}

fun <T> EditText.bindOutgoing(
    subject:Subject<T>,
    toT:(String)->T,
    validate: ((T)->T)? = null,
    toDisplayable:((T)->Any)? = null
) {
    this.focusChanges()
        .filter { !it }
        .withLatestFrom(this.textChanges()) { _, x -> x.toString() }
        .map { toT(it) }
        .doOnNext { // *side-effects are generally not recommended.. but I think it's okay here.
            // # Set the view's string, if it's invalid.
            val validatedText = it
                .let { if (validate==null) it else validate(it) }
                .let { if (toDisplayable==null) it else toDisplayable(it) }
                .toString()
            if (this.text.toString() != validatedText) this.setText(validatedText)
        }
        .subscribe(subject)
}

fun <T> TextView.bindIncoming(
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
