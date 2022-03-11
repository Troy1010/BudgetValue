package com.tminus1010.budgetvalue.replay_or_future.presentation

import com.tminus1010.budgetvalue._core.framework.source_objects.SourceList
import com.tminus1010.budgetvalue.transactions.app.interactor.TransactionsInteractor
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SetSearchTextsSharedVM @Inject constructor(
    transactionsInteractor: TransactionsInteractor,
) {
    // # State
    val searchTexts =
        transactionsInteractor.mostRecentUncategorizedSpend2
            .filterNotNull()
            .map { SourceList(it.description) }
            .stateIn(GlobalScope, SharingStarted.Eagerly, SourceList())
}