package com.tminus1010.budgetvalue.modules.plans

import com.tminus1010.budgetvalue.layer_data.Repo
import io.reactivex.rxjava3.core.Completable
import java.math.BigDecimal
import javax.inject.Inject

class ExpectedIncomeUseCasesImpl @Inject constructor(
    private val repo: Repo
): ExpectedIncomeUseCases {
    override fun fetchExpectedIncome(): BigDecimal =
        repo.fetchExpectedIncome().toBigDecimal()

    override fun pushExpectedIncome(expectedIncome: BigDecimal?): Completable =
        repo.pushExpectedIncome(expectedIncome.toString())
}