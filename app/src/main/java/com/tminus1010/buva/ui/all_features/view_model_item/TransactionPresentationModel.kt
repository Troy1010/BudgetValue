package com.tminus1010.buva.ui.all_features.view_model_item

import com.tminus1010.buva.R
import com.tminus1010.buva.all_layers.extensions.onNext
import com.tminus1010.buva.domain.Transaction
import com.tminus1010.tmcommonkotlin.core.extensions.toDisplayStr
import com.tminus1010.tmcommonkotlin.customviews.IHasToViewItemRecipe
import kotlinx.coroutines.flow.MutableSharedFlow

class TransactionPresentationModel(private val transaction: Transaction) {
    val backgroundColor get() = if (transaction.isCategorized) R.attr.colorBackground else R.attr.colorSecondary
    val date get() = transaction.date.toDisplayStr()
    val amount get() = transaction.amount.toString()
    val description get() = transaction.description.take(30)
    val userTryNavToTransaction = MutableSharedFlow<Transaction>()

    fun toVMItems(): List<IHasToViewItemRecipe> {
        return listOf(
            TextVMItem(text1 = date, backgroundColor = backgroundColor, onClick = { userTryNavToTransaction.onNext(transaction) }),
            TextVMItem(text1 = amount, backgroundColor = backgroundColor, onClick = { userTryNavToTransaction.onNext(transaction) }),
            TextVMItem(text1 = description, backgroundColor = backgroundColor, onClick = { userTryNavToTransaction.onNext(transaction) }),
        )
    }
}