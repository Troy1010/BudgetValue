package com.tminus1010.budgetvalue.layer_ui

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.extensions.unbox
import com.tminus1010.budgetvalue.layer_data.Repo
import com.tminus1010.budgetvalue.model_app.Category
import com.tminus1010.tmcommonkotlin.tuple.Box
import io.reactivex.rxjava3.schedulers.Schedulers
import java.time.format.DateTimeFormatter

class CategorizeVM(val repo: Repo, transactionsVM: TransactionsVM): ViewModel() {
    val transactionBox = transactionsVM.uncategorizedSpends
        .map { Box(it.getOrNull(0)) }
    val dateAsString = transactionBox
        .map { it.first?.date?.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))?:"" }
    fun setTransactionCategory(category: Category) {
        transactionBox
            .observeOn(Schedulers.io())
            .take(1)
            .unbox()
            .map { Pair(it.id, hashMapOf(category.name to it.amount)) }
            .flatMapCompletable { (id, categoryAmounts) -> repo.updateTransactionCategoryAmounts(id, categoryAmounts) }
            .subscribe()
    }
}