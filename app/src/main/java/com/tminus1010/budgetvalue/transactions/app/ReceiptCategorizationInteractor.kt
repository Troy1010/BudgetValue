package com.tminus1010.budgetvalue.transactions.app

import kotlinx.coroutines.flow.MutableStateFlow
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReceiptCategorizationInteractor @Inject constructor() {
    val currentChosenAmount = MutableStateFlow(BigDecimal("0"))
}