package com.tminus1010.buva.app

import com.tminus1010.buva.all_layers.extensions.isZero
import com.tminus1010.buva.domain.CategoryAmountsAndTotalWithValidation
import com.tminus1010.buva.domain.ValidationResult
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
                    caValidation = { category, amount ->
                        if ((amount ?: BigDecimal.ZERO) >= BigDecimal.ZERO)
                            ValidationResult.Success
                        else
                            ValidationResult.Failure
                    },
                    defaultAmountValidation = {
                        if (it?.isZero ?: true)
                            ValidationResult.Success
                        else
                            ValidationResult.Warning
                    },
                )
            }
}