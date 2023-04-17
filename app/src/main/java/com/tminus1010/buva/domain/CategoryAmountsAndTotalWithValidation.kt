package com.tminus1010.buva.domain

import java.math.BigDecimal

class CategoryAmountsAndTotalWithValidation(
    categoryAmounts: CategoryAmounts,
    total: BigDecimal,
    private val caValidation: (Category, BigDecimal?) -> ValidationResult,
    private val defaultAmountValidation: (BigDecimal?) -> ValidationResult,
) : CategoryAmountsAndTotal.FromTotal(categoryAmounts, total) {
    constructor(categoryAmountsAndTotal: CategoryAmountsAndTotal, caValidation: (Category, BigDecimal?) -> ValidationResult, defaultAmountValidation: (BigDecimal?) -> ValidationResult) : this(categoryAmountsAndTotal.categoryAmounts, categoryAmountsAndTotal.total, caValidation, defaultAmountValidation)

    val defaultValidationResult by lazy { defaultAmountValidation(defaultAmount) }
    val isAllValid by lazy { categoryAmounts.keys.map { validation(it) }.all { it != ValidationResult.Failure } && defaultValidationResult != ValidationResult.Failure }

    fun validation(category: Category): ValidationResult {
        return caValidation(category, categoryAmounts[category])
    }
}