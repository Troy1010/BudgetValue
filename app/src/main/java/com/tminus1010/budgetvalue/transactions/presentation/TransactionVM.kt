package com.tminus1010.budgetvalue.transactions.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.disposables
import com.tminus1010.budgetvalue.transactions.app.interactor.SaveTransactionInteractor
import com.tminus1010.budgetvalue.transactions.models.Transaction
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.subjects.PublishSubject
import javax.inject.Inject

@HiltViewModel
class TransactionVM @Inject constructor(
    private val saveTransactionInteractor: SaveTransactionInteractor,
) : ViewModel() {
    // # Input
    fun setup(transaction: Transaction) {
        this.transaction = transaction
    }

    fun userClearTransaction() {
        saveTransactionInteractor.saveTransaction(transaction.categorize(emptyMap()))
            .andThen(Completable.fromAction { navUp.onNext(Unit) })
            .observe(disposables)
    }

    // # Output
    val navUp = PublishSubject.create<Unit>()!!
    lateinit var transaction: Transaction
}