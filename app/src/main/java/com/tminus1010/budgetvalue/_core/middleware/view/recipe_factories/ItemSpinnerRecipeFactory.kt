package com.tminus1010.budgetvalue._core.middleware.view.recipe_factories

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.middleware.view.tmTableView3.IViewItemRecipe3
import com.tminus1010.budgetvalue._core.middleware.view.tmTableView3.ViewItemRecipe3__
import com.tminus1010.budgetvalue.databinding.ItemSpinnerBinding
import java.util.concurrent.atomic.AtomicBoolean

fun Fragment.itemSpinnerRF() = ItemSpinnerRecipeFactory(requireContext())

class ItemSpinnerRecipeFactory(private val context: Context) {
    private val inflate: (LayoutInflater) -> ItemSpinnerBinding = ItemSpinnerBinding::inflate
    fun <T> create(values: Array<T>, initialValue: T, lambda: (T) -> Unit): IViewItemRecipe3 {
        return ViewItemRecipe3__(context, inflate) { vb ->
            val adapter = ArrayAdapter(vb.root.context, R.layout.item_text_view_without_highlight, values)
            vb.spinner.adapter = adapter
            vb.spinner.setSelection(adapter.getPosition(initialValue))
            vb.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                var didFirstSelectionHappen = AtomicBoolean(false)
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    if (didFirstSelectionHappen.getAndSet(true))
                        lambda(vb.spinner.selectedItem as T)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) = Unit
            }
        }
    }
}