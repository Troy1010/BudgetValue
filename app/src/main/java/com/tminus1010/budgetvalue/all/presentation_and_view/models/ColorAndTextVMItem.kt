package com.tminus1010.budgetvalue.all.presentation_and_view.models

import android.content.Context
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet
import com.tminus1010.budgetvalue._core.middleware.view.recipe_factories.ItemColorAndTextRecipeFactory
import com.tminus1010.budgetvalue._core.middleware.view.tmTableView3.IViewItemRecipe3

/**
 * This decorator looks weird b/c part of its data is in dataSet.entries, while part of its data is in dataSet.
 * To solve that, the dataSet and an index is given, so that you can get everything you need.
 */
class ColorAndTextVMItem(val dataSet: IPieDataSet, val i: Int) {
    val text: String get() = dataSet.getEntryForIndex(i).label ?: ""
    val color get() = dataSet.getColor(i)

    fun toColorAndTextViewItemRecipe(context: Context): IViewItemRecipe3 {
        return ItemColorAndTextRecipeFactory(context).create(text, color)
    }
}