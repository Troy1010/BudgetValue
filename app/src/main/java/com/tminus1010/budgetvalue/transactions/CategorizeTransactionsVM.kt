package com.tminus1010.budgetvalue.transactions

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._core.extensions.toLiveData
import com.tminus1010.budgetvalue._core.middleware.unbox
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.transactions.domain.CategorizeTransactionsDomain
import com.tminus1010.budgetvalue.transactions.domain.ICategorizeTransactionsDomain
import com.tminus1010.tmcommonkotlin.rx.extensions.unbox
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class CategorizeTransactionsVM @Inject constructor(
    errorSubject: Subject<Throwable>,
    private val categorizeTransactionsDomain: CategorizeTransactionsDomain,
): ViewModel() {
    // # State
    val amountToCategorize = categorizeTransactionsDomain.transactionBox.unbox()
        .map { "Amount to categorize: $${it.amount}" }
        .toLiveData(errorSubject)
    val isTransactionAvailable = categorizeTransactionsDomain.transactionBox
        .map { it.unbox != null }
        .toLiveData(errorSubject)
    val date = categorizeTransactionsDomain.transactionBox
        .map { it.unbox?.date?.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) ?: "" }
        .toLiveData(errorSubject)
    val latestUncategorizedTransactionAmount = categorizeTransactionsDomain.transactionBox
        .map { it.unbox?.defaultAmount?.toString() ?: "" }
        .toLiveData(errorSubject)
    val latestUncategorizedTransactionDescription = categorizeTransactionsDomain.transactionBox
        .map { it.unbox?.description ?: "" }
        .toLiveData(errorSubject)
    // # Intents
    fun finishTransactionWithCategory(category: Category) {
        categorizeTransactionsDomain.finishTransactionWithCategory(category)
    }
}