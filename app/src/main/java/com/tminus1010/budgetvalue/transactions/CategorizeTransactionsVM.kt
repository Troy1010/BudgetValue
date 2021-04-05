package com.tminus1010.budgetvalue.transactions

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._core.extensions.toLiveData
import com.tminus1010.budgetvalue._core.middleware.unbox
import com.tminus1010.budgetvalue.transactions.domain.CategorizeTransactionsDomain
import com.tminus1010.budgetvalue.transactions.domain.ICategorizeTransactionsDomain
import com.tminus1010.tmcommonkotlin.rx.extensions.unbox
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.subjects.PublishSubject
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class CategorizeTransactionsVM @Inject constructor(
    categorizeTransactionsDomain: CategorizeTransactionsDomain
): ViewModel(), ICategorizeTransactionsDomain by categorizeTransactionsDomain {
    // # Subjects
    private val errorSubject = PublishSubject.create<Throwable>()
        .also { it.subscribe() }
    // # State
    val amountToCategorize = categorizeTransactionsDomain.transactionBox
        .unbox().map { "Amount to categorize: $${it.amount}" }
        .toLiveData(errorSubject)
    val isTransactionAvailable = categorizeTransactionsDomain.transactionBox
        .map { it.unbox != null }
        .toLiveData(errorSubject)
    val date = categorizeTransactionsDomain.transactionBox
        .map { it.unbox?.date?.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) ?: "" }
        .toLiveData(errorSubject)
    // # Intents
}