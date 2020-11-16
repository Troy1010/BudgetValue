package com.example.budgetvalue.layer_ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetvalue.layer_data.Repo
import com.example.budgetvalue.model_app.Category
import com.tminus1010.tmcommonkotlin.tuple.Box
import com.tminus1010.tmcommonkotlin_rx.toBehaviorSubject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

class CategorizeVM(val repo: Repo, transactionsVM: TransactionsVM): ViewModel() {
    val transactionBox = transactionsVM.uncategorizedSpends
        .map { Box(it.getOrNull(0)) }
        .toBehaviorSubject()
    val dateAsString = transactionBox
        .map { it.first?.date?.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))?:"" }
        .toBehaviorSubject()
    fun setTransactionCategory(category: Category) {
        val transaction = transactionBox.value?.first ?: return
        viewModelScope.launch(Dispatchers.IO) {
            transaction.categoryAmounts.clear()
            transaction.categoryAmounts[category.name] = transaction.amount
            repo.updateTransaction(transaction)
        }
    }
}