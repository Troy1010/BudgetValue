package com.tminus1010.budgetvalue.transactions.presentation

import com.tminus1010.budgetvalue.all_features.all_layers.extensions.onNext
import com.tminus1010.budgetvalue.all_features.ui.all_features.model.TextVMItem
import com.tminus1010.budgetvalue.transactions.data.repo.TransactionsRepo
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
    val userSubmitDescription = MutableSharedFlow<String>()
    fun userSubmitDescription(s: String) {
        userSubmitDescription.onNext(s)
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
            .map { it.map { listOf(TextVMItem(it.description, onClick = { userSubmitDescription(it.description) })) } }
}