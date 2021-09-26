package com.tminus1010.budgetvalue.all.presentation_and_view._models

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.tminus1010.budgetvalue.R
import java.util.concurrent.atomic.AtomicBoolean

@Suppress("UNCHECKED_CAST")
class SpinnerVMItem<T>(private val values: Array<T>, private val initialValue: T, private val lambda: (T) -> Unit) {
    init {
        if (values.isEmpty()) error("Values was empty, and that is unsupported atm. Perhaps an empty set of values should be supported..?")
    }

    fun bind(spinner: Spinner) {
        val adapter = ArrayAdapter(spinner.context, R.layout.item_text_view_without_highlight, values)
        spinner.adapter = adapter
        spinner.setSelection(adapter.getPosition(initialValue ?: values[0]))
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            var didFirstSelectionHappen = AtomicBoolean(false)
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (didFirstSelectionHappen.getAndSet(true))
                    lambda(spinner.selectedItem as T)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
    }
}

fun <T> Spinner.bind(spinnerVMItem: SpinnerVMItem<T>) = spinnerVMItem.bind(this)