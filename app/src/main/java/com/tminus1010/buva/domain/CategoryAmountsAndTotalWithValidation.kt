package com.tminus1010.buva.domain

import java.math.BigDecimal

class CategoryAmountsAndTotalWithValidation(
    categoryAmounts: CategoryAmounts,
    total: BigDecimal,
    private val caValidation: (BigDecimal?) -> Validation,
    private val defaultAmountValidation: (BigDecimal?) -> Validation,
) : CategoryAmountsAndTotal.FromTotal(categoryAmounts, total) {
    constructor(categoryAmountsAndTotal: CategoryAmountsAndTotal, caValidation: (BigDecimal?) -> Validation, defaultAmountValidation: (BigDecimal?) -> Validation) : this(categoryAmountsAndTotal.categoryAmounts, categoryAmountsAndTotal.total, caValidation, defaultAmountValidation)

    val defaultValidationResult by lazy { defaultAmountValidation(defaultAmount) }
    val isAllValid by lazy { categoryAmounts.keys.map { validation(it) }.all { it != Validation.Failure } && defaultValidationResult != Validation.Failure }

    fun validation(category: Category): Validation {
        return caValidation(categoryAmounts[category])
    }
}