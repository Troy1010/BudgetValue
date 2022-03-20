package com.tminus1010.budgetvalue.transactions.presentation.model

import android.content.Context
import android.view.LayoutInflater
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.all_features.all_layers.extensions.easyText
import com.tminus1010.budgetvalue.all_features.all_layers.extensions.getColorByAttr
import com.tminus1010.tmcommonkotlin.misc.tmTableView.IViewItemRecipe3
import com.tminus1010.budgetvalue.all_features.framework.view.tmTableView3.ViewItemRecipeFactory3
import com.tminus1010.budgetvalue.databinding.ItemTextViewBinding
import com.tminus1010.budgetvalue.transactions.app.Transaction
import com.tminus1010.tmcommonkotlin.core.extensions.toDisplayStr
import io.reactivex.rxjava3.subjects.PublishSubject

class TransactionPresentationModel(private val transaction: Transaction) {
    val backgroundColor get() = if (transaction.isCategorized) R.attr.colorBackground else R.attr.colorSecondary
    val date get() = transaction.date.toDisplayStr()
    val amount get() = transaction.amount.toString()
    val description get() = transaction.description.take(30)
    val userTryNavToTransaction by lazy { PublishSubject.create<Transaction>() }

    fun toViewItemRecipes(context: Context): List<IViewItemRecipe3> {
        val viewItemRecipeFactory = ViewItemRecipeFactory3<ItemTextViewBinding, String>(
            { ItemTextViewBinding.inflate(LayoutInflater.from(context)) },
            { s, vb, _ ->
                vb.textview.easyText = s
                vb.root.setBackgroundColor(context.theme.getColorByAttr(backgroundColor))
                vb.root.setOnClickListener { userTryNavToTransaction.onNext(transaction) }
            }
        )
        return listOf(
            viewItemRecipeFactory.createOne(date),
            viewItemRecipeFactory.createOne(amount),
            viewItemRecipeFactory.createOne(description),
        )
    }
}