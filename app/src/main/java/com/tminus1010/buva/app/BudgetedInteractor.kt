package com.tminus1010.buva.app

import com.tminus1010.buva.all_layers.extensions.isZero
import com.tminus1010.buva.domain.CategoryAmountsAndTotalWithValidation
import com.tminus1010.buva.domain.Validation
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
                    caValidation = { if ((it ?: BigDecimal.ZERO) >= BigDecimal.ZERO) Validation.Success else Validation.Failure }, // TODO: Doesn't really make sense to have caValidation when each category is validated in its own way.
                    defaultAmountValidation = { if (it?.isZero ?: true) Validation.Success else Validation.Warning },
                )
            }
}