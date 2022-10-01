package com.tminus1010.buva.ui.all_features.view_model_item

import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.tminus1010.buva.R
import com.tminus1010.buva.all_layers.extensions.onNext
import com.tminus1010.tmcommonkotlin.customviews.IViewItemRecipe3
import com.tminus1010.tmcommonkotlin.customviews.ViewItemRecipe3
import com.tminus1010.tmcommonkotlin.customviews.IHasToViewItemRecipe
import com.tminus1010.buva.databinding.ItemSpinnerBinding
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.atomic.AtomicBoolean

@Suppress("UNCHECKED_CAST")
class SpinnerVMItem<T>(
    private val values: Array<T>,
    private val initialValue: T? = null,
    private val onNewItem: (T) -> Unit
) : IHasToViewItemRecipe {
    constructor(values: Array<T>, behaviorSubject: BehaviorSubject<T>) : this(values, behaviorSubject.value, behaviorSubject::onNext)
    constructor(values: Array<T>, mutableStateFlow: MutableStateFlow<T>) : this(values, mutableStateFlow.value, mutableStateFlow::onNext)

    init {
        if (values.isEmpty()) error("Values was empty, and that is unsupported atm. Perhaps an empty set of values should be supported..?")
    }

    fun bind(spinner: Spinner) {
        val adapter = ArrayAdapter(spinner.context, R.layout.item_text_view_without_highlight, values)
        spinner.adapter = adapter
        spinner.setSelection(adapter.getPosition(initialValue ?: values[0]))
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            var didFirstSelectionHappen = AtomicBoolean(false)
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (didFirstSelectionHappen.getAndSet(true))
                    onNewItem(spinner.selectedItem as T)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
    }

    override fun toViewItemRecipe(context: Context): IViewItemRecipe3 {
        return ViewItemRecipe3(context, ItemSpinnerBinding::inflate) { vb ->
            bind(vb.spinner)
        }
    }
}

fun <T> Spinner.bind(spinnerVMItem: SpinnerVMItem<T>) = spinnerVMItem.bind(this)