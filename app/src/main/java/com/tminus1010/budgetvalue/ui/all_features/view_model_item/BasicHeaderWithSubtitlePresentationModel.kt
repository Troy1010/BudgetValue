package com.tminus1010.budgetvalue.ui.all_features.view_model_item

import android.content.Context
import android.view.View
import com.tminus1010.tmcommonkotlin.misc.tmTableView.IViewItemRecipe3
import com.tminus1010.tmcommonkotlin.misc.tmTableView.ViewItemRecipe3
import com.tminus1010.tmcommonkotlin.misc.tmTableView.IHasToViewItemRecipe
import com.tminus1010.budgetvalue.databinding.ItemHeaderWithSubtitleBinding
import com.tminus1010.tmcommonkotlin.view.NativeText

class BasicHeaderWithSubtitlePresentationModel(val title: String, val subTitle: NativeText?, val onLongClick: (View) -> Unit) : IHasToViewItemRecipe {
    override fun toViewItemRecipe(context: Context): IViewItemRecipe3 {
        return ViewItemRecipe3(context, ItemHeaderWithSubtitleBinding::inflate) { vb ->
            vb.textviewHeader.text = title
            vb.textviewSubtitle.text = subTitle?.toCharSequence(context)
            vb.root.setOnLongClickListener { onLongClick(vb.root); true }
        }
    }
}