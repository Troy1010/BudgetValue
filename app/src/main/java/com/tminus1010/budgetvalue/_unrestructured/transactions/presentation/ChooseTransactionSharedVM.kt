package com.tminus1010.budgetvalue._unrestructured.transactions.presentation

import com.tminus1010.budgetvalue._unrestructured.transactions.app.Transaction
import com.tminus1010.budgetvalue._unrestructured.transactions.data.repo.TransactionsRepo
import com.tminus1010.budgetvalue.all_layers.extensions.onNext
import com.tminus1010.budgetvalue.ui.all_features.model.TextVMItem
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChooseTransactionSharedVM @Inject constructor(
    transactionsRepo: TransactionsRepo,
) {
    // # User Intents
    val userSubmitTransaction = MutableSharedFlow<Transaction>()
    fun userSubmitTransaction(transaction: Transaction) {
        userSubmitTransaction.onNext(transaction)
        navUp.onNext()
    }

    // # Events
    val navUp = MutableSharedFlow<Unit>()

    // # State
    val isNoItemsMsgVisible = BehaviorSubject.createDefault(false)
    val recipeGrid =
        transactionsRepo.transactionsAggregate2
            .map { transactionsAggregate ->
                transactionsAggregate.transactions
                    .let { if (transactionsAggregate.mostRecentUncategorizedSpend == null) it else listOf(transactionsAggregate.mostRecentUncategorizedSpend!!) + it }
                    .distinctBy { it.description }
            }
            .map { it.map { listOf(TextVMItem(it.description, onClick = { userSubmitTransaction(it) })) } }
}