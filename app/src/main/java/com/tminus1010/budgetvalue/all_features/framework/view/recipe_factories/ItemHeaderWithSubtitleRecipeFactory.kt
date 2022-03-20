package com.tminus1010.budgetvalue.all_features.framework.view.recipe_factories

import android.content.Context
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import com.tminus1010.tmcommonkotlin.misc.tmTableView.IViewItemRecipe3
import com.tminus1010.tmcommonkotlin.misc.tmTableView.ViewItemRecipe3
import com.tminus1010.budgetvalue.databinding.ItemHeaderWithSubtitleBinding
import com.tminus1010.tmcommonkotlin.misc.extensions.bind
import io.reactivex.rxjava3.core.Observable

fun Fragment.itemHeaderWithSubtitleRF() = ItemHeaderWithSubtitleRecipeFactory(requireContext())

class ItemHeaderWithSubtitleRecipeFactory(private val context: Context) {
    private val inflate: (LayoutInflater) -> ItemHeaderWithSubtitleBinding = ItemHeaderWithSubtitleBinding::inflate
    fun create(header: String, subtitle: Observable<String>): IViewItemRecipe3 {
        return ViewItemRecipe3(context, inflate) { vb ->
            vb.textviewHeader.text = header
            vb.textviewSubtitle.bind(subtitle) { text = it }
        }
    }
}