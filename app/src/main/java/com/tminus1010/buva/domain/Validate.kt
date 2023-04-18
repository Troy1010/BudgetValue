package com.tminus1010.buva.domain

import com.tminus1010.buva.all_layers.extensions.isZero
import java.math.BigDecimal

object Validate {
    fun resetMax(bigDecimal: BigDecimal?): ValidationResult {
        return if (bigDecimal == null || !bigDecimal.isZero)
            ValidationResult.Success
        else
            ValidationResult.Failure()
    }
}