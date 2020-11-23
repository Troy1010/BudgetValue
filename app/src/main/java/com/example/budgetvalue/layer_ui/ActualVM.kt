package com.example.budgetvalue.layer_ui

import androidx.lifecycle.ViewModel
import com.example.budgetvalue.layer_data.Repo
import com.example.budgetvalue.model_app.Category
import com.tminus1010.tmcommonkotlin.tuple.Box
import io.reactivex.rxjava3.schedulers.Schedulers
import java.time.format.DateTimeFormatter

class ActualVM(val repo: Repo, transactionsVM: TransactionsVM): ViewModel() {
    val transactionBox = transactionsVM.uncategorizedSpends
        .map { Box(it.getOrNull(0)) }
    val dateAsString = transactionBox
        .map { it.first?.date?.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))?:"" }
    fun setTransactionCategory(category: Category) {
        transactionBox
            .observeOn(Schedulers.io())
            .take(1)
            .filter { it.first != null }
            .map { it.first!! }
            .subscribe { transaction ->
                val categoryAmounts = hashMapOf(Pair(category.name, transaction.amount))
                repo.updateTransactionCategoryAmounts(transaction.id, categoryAmounts)
            }
    }
}