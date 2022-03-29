package com.tminus1010.budgetvalue.ui.all_features.view_model_item

import android.content.Context
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.domain.Transaction
import com.tminus1010.tmcommonkotlin.core.extensions.toDisplayStr
import com.tminus1010.tmcommonkotlin.misc.tmTableView.IViewItemRecipe3
import io.reactivex.rxjava3.subjects.PublishSubject

class TransactionPresentationModel(private val transaction: Transaction) {
    val backgroundColor get() = if (transaction.isCategorized) R.attr.colorBackground else R.attr.colorSecondary
    val date get() = transaction.date.toDisplayStr()
    val amount get() = transaction.amount.toString()
    val description get() = transaction.description.take(30)
    val userTryNavToTransaction by lazy { PublishSubject.create<Transaction>() }

    fun toViewItemRecipes(context: Context): List<IViewItemRecipe3> {
        return listOf(
            TextVMItem(text1 = date, backgroundColor = backgroundColor, onClick = { userTryNavToTransaction.onNext(transaction) }).toViewItemRecipe(context),
            TextVMItem(text1 = amount, backgroundColor = backgroundColor, onClick = { userTryNavToTransaction.onNext(transaction) }).toViewItemRecipe(context),
            TextVMItem(text1 = description, backgroundColor = backgroundColor, onClick = { userTryNavToTransaction.onNext(transaction) }).toViewItemRecipe(context),
        )
    }
}