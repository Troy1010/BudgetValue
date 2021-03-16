package com.tminus1010.budgetvalue.layer_ui.misc

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
    bindOutgoing(subject, toT, validate)
}

fun <T> EditText.bind(
    incoming:Observable<T>,
    outgoing:Subject<T>,
    toT:(String)->T,
    validate: ((T)->T)? = null,
    toDisplayable:((T)->Any)? = null
) {
    bindIncoming(incoming, toDisplayable)
    bindOutgoing(outgoing, toT, validate, toDisplayable)
}

// TODO("This will push unchanged incoming values")
fun <T> EditText.bindOutgoing(
    subject:Subject<T>,
    toT:(String)->T,
    validate: ((T)->T)? = null,
    toDisplayable: ((T) -> Any)? = null
) {
    this.focusChanges()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(AndroidSchedulers.mainThread())
        .skip(1) //*focusChanges always starts with false, for some reason.
        .filter { !it }
        .withLatestFrom(this.textChanges()) { _, x -> x.toString() }
        .map { toT(it) }
        .map { if (validate==null) it else validate(it) }
        .publish().refCount()
        .also { if (toDisplayable!=null) this.bindIncoming(it, toDisplayable) }
        .distinctUntilChanged()
        .subscribe(subject)
}

fun <T> TextView.bindIncoming(
    observable: Observable<T>,
    toDisplayable:((T)->Any)? = null
): Disposable {
    return observable
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(AndroidSchedulers.mainThread())
        .filter { this.layoutParams!=null } // *An error happens if you try to set text while layoutParams is null. But perhaps this filter should be moved elsewhere.
        .map { if (toDisplayable!=null) toDisplayable(it).toString() else it.toString() }
        .subscribe { this.text = it }
}

fun Button.setOnClickListener(subject: Subject<Unit>) {
    setOnClickListener { subject.onNext(Unit) }
}
