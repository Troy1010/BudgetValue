package com.example.budgetvalue.layer_ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetvalue.layer_data.Repo
import com.example.budgetvalue.model_app.Category
import com.tminus1010.tmcommonkotlin_rx.toBehaviorSubject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

class CategorizeVM(val repo: Repo, transactionsVM: TransactionsVM): ViewModel() {
    val transaction = transactionsVM.uncategorizedSpends
        .map { it[0] }
        .toBehaviorSubject()
    val dateAsString = transaction
        .map { it.date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) }
        .toBehaviorSubject()
    fun setTransactionCategory(category: Category) {
        val transaction_ = transaction.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            transaction_.categoryAmounts.clear()
            transaction_.categoryAmounts[category.name] = transaction_.amount
            repo.updateTransaction(transaction_)
        }
    }
}