package com.tminus1010.budgetvalue.transactions.presentation

import com.tminus1010.budgetvalue._core.presentation.model.TextVMItem
import com.tminus1010.budgetvalue.transactions.data.repo.TransactionsRepo
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChooseTransactionSharedVM @Inject constructor(
    transactionsRepo: TransactionsRepo,
) {
    // # User Intents
    fun userSubmitDescription(s: String) {

    }

    // # State
    val isNoItemsMsgVisible = BehaviorSubject.createDefault(false)
    val recipeGrid =
        transactionsRepo.transactionsAggregate2
            .map { transactionsAggregate ->
                transactionsAggregate.transactions
                    .let { if (transactionsAggregate.mostRecentUncategorizedSpend == null) it else listOf(transactionsAggregate.mostRecentUncategorizedSpend!!) + it }
                    .distinctBy { it.description }
            }
            .map { it.map { listOf(TextVMItem(it.description)) } }
}