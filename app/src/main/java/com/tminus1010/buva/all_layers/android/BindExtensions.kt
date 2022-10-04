package com.tminus1010.buva.all_layers.android

import android.view.inputmethod.EditorInfo
import android.widget.EditText
import com.tminus1010.tmcommonkotlin.androidx.extensions.addOnFocusChangeListenerDecoration
import com.tminus1010.tmcommonkotlin.androidx.extensions.isRemovingViews
import com.tminus1010.tmcommonkotlin.androidx.extensions.parents


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
    addOnFocusChangeListenerDecoration { _, hasFocus ->
        if (!hasFocus && parents.none { it.isRemovingViews }) onDone(text.toString())
    }
}
