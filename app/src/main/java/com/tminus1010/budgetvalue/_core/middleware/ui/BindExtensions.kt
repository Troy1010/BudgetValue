package com.tminus1010.budgetvalue._core.middleware.ui

import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.jakewharton.rxbinding4.view.focusChanges
import com.jakewharton.rxbinding4.widget.TextViewEditorActionEvent
import com.jakewharton.rxbinding4.widget.editorActionEvents
import com.jakewharton.rxbinding4.widget.textChanges
import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.tmcommonkotlin.misc.extensions.easyGetLayoutParams
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.Subject


// * I'm not sure if this is the best idea. It might be better to have 2 different incoming and outgoing streams.
fun <T> EditText.bind(
    subject: Subject<T>,
    toT: (String) -> T,
    validate: ((T) -> T)? = null,
    toDisplayable: ((T) -> Any)? = null
) {
    bindIncoming(subject, toDisplayable)
    bindOutgoing(subject, toT, validate)
}

fun <T> EditText.bind(
    incoming: Observable<T>,
    outgoing: Subject<T>,
    toT: (String) -> T,
    validate: ((T) -> T)? = null,
    toDisplayable: ((T) -> Any)? = null
) {
    bindIncoming(incoming, toDisplayable)
    bindOutgoing(outgoing, toT, validate, toDisplayable)
}

// TODO("This will push unchanged incoming values")
fun <T> EditText.bindOutgoing(
    subject: Subject<T>,
    toT: (String) -> T,
    validate: ((T) -> T)? = null,
    toDisplayable: ((T) -> Any?)? = null
) {
    Observable.merge(
        editorActionEvents2 { false }.filter { it.actionId == EditorInfo.IME_ACTION_DONE },
        focusChanges().skip(1).filter { !it }
    ).subscribeOn(AndroidSchedulers.mainThread())
        .observeOn(AndroidSchedulers.mainThread())
        .withLatestFrom(this.textChanges()) { _, x -> x.toString() }
        .map { toT(it) }
        .map { if (validate!=null) validate(it) else it }
        .publish().refCount()
        .also { if (toDisplayable!=null) this.bindIncoming(it, toDisplayable) }
        .distinctUntilChanged()
        .subscribe(subject)
}

// Transform to output, validate, transform back

fun EditText.onDone(onDone: (String) -> Unit) {
    setOnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            onDone(text.toString())
            false
        } else true
    }
    setOnFocusChangeListener { _, hasFocus ->
        if (!hasFocus) onDone(text.toString())
    }
}

fun <T> TextView.bindIncoming(
    observable: Observable<T>,
    toDisplayable: ((T) -> Any?)? = null
): Disposable {
    return observable
        .observeOn(AndroidSchedulers.mainThread())
        .filter { this.layoutParams!=null } // *An error happens if you try to set text while layoutParams is null. But perhaps this filter should be moved elsewhere.
        .map { if (toDisplayable!=null) toDisplayable(it).toString() else it.toString() }
        .subscribe { this.text = it }
}

// editorActionEvents does not emit when handled=true:
// https://github.com/JakeWharton/RxBinding/pull/378
// This is a workaround.
fun EditText.editorActionEvents2(handled: (TextViewEditorActionEvent) -> Boolean = { true }): Observable<TextViewEditorActionEvent> =
    Observable.create { downstream ->
        editorActionEvents { downstream.onNext(it); handled(it) }
            .subscribe({}, { downstream.onError(it) }) { downstream.onComplete() }
            .also { downstream.setDisposable(it) }
    }
