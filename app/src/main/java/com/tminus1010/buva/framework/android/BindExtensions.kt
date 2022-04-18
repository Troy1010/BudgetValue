package com.tminus1010.buva.framework.android

import android.view.inputmethod.EditorInfo
import android.widget.EditText


fun EditText.onDone(onDone: (String) -> Unit) {
    if (this is IOnEditorActionListener)
        onEditorActionListener.subscribe { (_, actionId, _) -> // disposable is unhandled
            if (actionId == EditorInfo.IME_ACTION_DONE)
                onDone(text.toString())
        }
    else
        setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                onDone(text.toString())
                false
            } else true
        }
    if (this is IOnFocusChangedOwner)
        onFocusChanged.subscribe { (_, hasFocus) -> // disposable is unhandled
            if (!hasFocus) onDone(text.toString())
        }
    else
        setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) onDone(text.toString())
        }
}
