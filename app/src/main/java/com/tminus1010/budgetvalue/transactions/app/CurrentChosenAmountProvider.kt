package com.tminus1010.budgetvalue.transactions.app

import kotlinx.coroutines.flow.MutableStateFlow
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentChosenAmountProvider @Inject constructor() {
    val currentChosenAmount = MutableStateFlow(BigDecimal("0"))
}