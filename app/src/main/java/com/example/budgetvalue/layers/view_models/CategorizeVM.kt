package com.example.budgetvalue.layers.view_models

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetvalue.layers.data_layer.Repo
import com.example.budgetvalue.models.Category
import com.example.budgetvalue.models.Transaction
import com.example.budgetvalue.util.toBehaviorSubject
import com.example.budgetvalue.util.toLiveData2
import com.example.tmcommonkotlin.logz
import com.google.gson.Gson
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter

class CategorizeVM(val repo: Repo, transactionsVM: TransactionsVM): ViewModel() {
    val transaction = transactionsVM.uncategorizedSpends
        .map {
            it[0]
        }.toBehaviorSubject()
    val transaction_ = transaction.toLiveData2()
    val dateAsString = transaction
        .map {
            it.date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))
        }.toBehaviorSubject()
    fun setTransactionCategory(category: Category) {
        val transaction_ = transaction.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            transaction_.categoryAmounts.clear()
            transaction_.categoryAmounts[category.name] = transaction_.amount
            repo.updateTransaction(transaction_)
        }
    }
}