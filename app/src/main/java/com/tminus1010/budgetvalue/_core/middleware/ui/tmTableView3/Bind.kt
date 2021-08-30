package com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.extensions.bind
import com.tminus1010.budgetvalue._core.extensions.easyText
import com.tminus1010.budgetvalue._core.middleware.ui.onDone
import com.tminus1010.budgetvalue.databinding.*
import io.reactivex.rxjava3.core.Observable
import java.util.concurrent.atomic.AtomicBoolean

val bindItemHeaderBinding = { d: String, vb: ItemHeaderBinding ->
    vb.textview.text = d
}
val bindItemTextViewBinding = { d: String, vb: ItemTextViewBinding ->
    vb.textview.text = d
}

fun bindItemEditTextBinding(lambda: (String) -> Unit) = { d: Observable<String>, vb: ItemEditTextBinding ->
    vb.edittext.bind(d) { easyText = it }
    vb.edittext.onDone(lambda)
}

fun bindItemMoneyEditTextBinding(lambda: (String) -> Unit) = { d: String, vb: ItemMoneyEditTextBinding ->
    vb.moneyedittext.easyText = d
    vb.moneyedittext.onDone(lambda)
}

fun bindItemMoneyEditTextBinding2(lambda: (String) -> Unit) = { d: Observable<String>, vb: ItemMoneyEditTextBinding ->
    vb.moneyedittext.bind(d) { easyText = it }
    vb.moneyedittext.onDone(lambda)
}

@Suppress("UNCHECKED_CAST")
fun <T : Any?> bindItemSpinnerBinding(values: Array<T>, lambda: (T) -> Unit) = { d: T, vb: ItemSpinnerBinding ->
    val adapter = ArrayAdapter(vb.root.context, R.layout.item_text_view_without_highlight, values)
    vb.spinner.adapter = adapter
    vb.spinner.setSelection(adapter.getPosition(d))
    vb.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        var didFirstSelectionHappen = AtomicBoolean(false)
        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            if (didFirstSelectionHappen.getAndSet(true))
                lambda(vb.spinner.selectedItem as T)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) = Unit
    }
}

fun bindItemCheckboxBinding(value: String, lambda: (String) -> Unit) = { d: Observable<Boolean>, vb: ItemCheckboxBinding ->
    vb.checkbox.bind(d) {
        isChecked = it
        isEnabled = !it
    }
    vb.checkbox.setOnCheckedChangeListener { _, isChecked ->
        if (isChecked) lambda(value)
    }
}
