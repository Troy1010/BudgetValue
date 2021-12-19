package com.tminus1010.budgetvalue.transactions.app

import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationEventProvider @Inject constructor() {
    val showChooseAmount = MutableSharedFlow<Unit>()
    val showChooseCategory = MutableSharedFlow<Unit>()
}