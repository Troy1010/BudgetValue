package com.tminus1010.budgetvalue.layer_domain.use_cases

import io.reactivex.rxjava3.core.Completable
import java.math.BigDecimal

interface ExpectedIncomeUseCases {
    fun fetchExpectedIncome(): BigDecimal
    fun pushExpectedIncome(expectedIncome: BigDecimal?): Completable
}