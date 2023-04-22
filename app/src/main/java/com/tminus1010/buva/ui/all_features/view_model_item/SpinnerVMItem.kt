package com.tminus1010.buva.ui.all_features.view_model_item

import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.lifecycle.*
import com.tminus1010.buva.R
import com.tminus1010.buva.all_layers.extensions.onNext
import com.tminus1010.buva.all_layers.extensions.value
import com.tminus1010.buva.databinding.ItemSpinnerBinding
import com.tminus1010.tmcommonkotlin.customviews.IViewItemRecipe3
import com.tminus1010.tmcommonkotlin.customviews.ViewItemRecipe3
import com.tminus1010.tmcommonkotlin.customviews.ViewItemRecipeFactory
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

@Suppress("UNCHECKED_CAST")
class SpinnerVMItem<T>(
    private val values: Array<T>,
    private val onNewItem: (T) -> Unit,
    private val valueFlow: Flow<T>? = null,
    private val initialValue: T? = null,
) : ViewItemRecipeFactory {
    constructor(values: Array<T>, behaviorSubject: BehaviorSubject<T>) : this(values, behaviorSubject::onNext, initialValue = behaviorSubject.value)
    constructor(values: Array<T>, mutableStateFlow: MutableSharedFlow<T>) : this(values, mutableStateFlow::onNext, valueFlow = mutableStateFlow, initialValue = mutableStateFlow.value)
    constructor(values: Array<T>, mutableStateFlow: SharedFlow<T>, onNewItem: (T) -> Unit) : this(values, onNewItem, valueFlow = mutableStateFlow, initialValue = mutableStateFlow.value)

    init {
        if (values.isEmpty()) error("Values was empty, and that is unsupported atm. Perhaps an empty set of values should be supported..?")
    }

    fun bind(spinner: Spinner) {
        val adapter = ArrayAdapter(spinner.context, R.layout.item_text_view_without_highlight, values)
        spinner.adapter = adapter
        valueFlow?.observe2(spinner.findViewTreeLifecycleOwner()!!) { spinner.setSelection(adapter.getPosition(it)) }
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

    // TODO: Should normal .observe also not use reified?
    private fun <T> Flow<T>.observe2(lifecycleOwner: LifecycleOwner, lifecycleState: Lifecycle.State = Lifecycle.State.STARTED, lambda: suspend (T) -> Unit) {
        lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.repeatOnLifecycle(lifecycleState) {
                this@observe2.collect { lambda(it) }
            }
        }
    }
}

fun <T> Spinner.bind(spinnerVMItem: SpinnerVMItem<T>) = spinnerVMItem.bind(this)