package com.tminus1010.budgetvalue.framework.view.tmTableView3

import android.content.Context
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import com.tminus1010.budgetvalue.all_layers.extensions.easyText
import com.tminus1010.budgetvalue.databinding.ItemEmptyBinding
import com.tminus1010.budgetvalue.databinding.ItemHeaderBinding
import com.tminus1010.budgetvalue.databinding.ItemTextViewBinding
import com.tminus1010.budgetvalue.databinding.ItemTitledDividerBinding
import com.tminus1010.tmcommonkotlin.misc.extensions.bind
import io.reactivex.rxjava3.core.Observable

@Deprecated("use commonlib's TMTableView")
interface IRecipeFactories {
    val titledDivider: ViewItemRecipeFactory3<ItemTitledDividerBinding, String>
    val textView: ViewItemRecipeFactory3<ItemTextViewBinding, Any>
    val header: ViewItemRecipeFactory3<ItemHeaderBinding, String>
    val textViewWithLifecycle: ViewItemRecipeFactory3<ItemTextViewBinding, Observable<String>?>
}

@Deprecated("use viewItemRecipe pattern instead")
val Fragment.recipeFactories
    get() = object : IRecipeFactories {
        override val titledDivider: ViewItemRecipeFactory3<ItemTitledDividerBinding, String>
            get() = ViewItemRecipeFactory3(
                { ItemTitledDividerBinding.inflate(LayoutInflater.from(requireContext())) },
                { d: String, vb, _ -> vb.textview.easyText = d }
            )

        override val textView: ViewItemRecipeFactory3<ItemTextViewBinding, Any>
            get() = ViewItemRecipeFactory3(
                { ItemTextViewBinding.inflate(LayoutInflater.from(requireContext())) },
                { d: Any, vb, _ -> vb.textview.easyText = d.toString() }
            )

        override val header: ViewItemRecipeFactory3<ItemHeaderBinding, String>
            get() = ViewItemRecipeFactory3(
                { ItemHeaderBinding.inflate(LayoutInflater.from(requireContext())) },
                { d: String, vb, _ -> vb.textview.easyText = d }
            )

        override val textViewWithLifecycle: ViewItemRecipeFactory3<ItemTextViewBinding, Observable<String>?>
            get() = ViewItemRecipeFactory3(
                { ItemTextViewBinding.inflate(LayoutInflater.from(requireContext())) },
                { d, vb, lifecycle ->
                    if (d == null) return@ViewItemRecipeFactory3
                    vb.textview.bind(d, lifecycle) { easyText = it }
                }
            )
    }

@Deprecated("use commonlib's TMTableView")
fun NothingRecipe(context: Context) = ViewItemRecipe3<ItemEmptyBinding, Unit?>(
    { ItemEmptyBinding.inflate(LayoutInflater.from(context)) },
    { _, _, _ -> }
)
