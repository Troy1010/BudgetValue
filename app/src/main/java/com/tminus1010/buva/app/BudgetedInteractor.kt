package com.tminus1010.buva.app

import com.tminus1010.buva.domain.CategoryAmountsAndTotalWithValidation
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import javax.inject.Inject

class BudgetedInteractor @Inject constructor(
    historyInteractor: HistoryInteractor,
) {
    val budgeted =
        historyInteractor.entireHistory
            .map { categoryAmountsAndTotalsAggregate ->
                CategoryAmountsAndTotalWithValidation(
                    categoryAmountsAndTotal = categoryAmountsAndTotalsAggregate.addedTogether,
                    caValidation = { (it ?: BigDecimal.ZERO) >= BigDecimal.ZERO },
                    defaultAmountValidation = { true },
                )
            }
}