package com.tminus1010.buva.domain

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.tminus1010.buva.all_layers.extensions.isZero
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

@Suppress("PROPERTY_WONT_BE_SERIALIZED")
@Parcelize
@Entity
data class Category(
    @PrimaryKey
    val name: String,
    val defaultAmountFormula: AmountFormula = AmountFormula.Value(BigDecimal.ZERO),
    val isRequired: Boolean = false,
    val resetStrategy: ResetStrategy = ResetStrategy.Basic(BigDecimal.ZERO), // TODO: Perhaps ResetStrategy should be a part of the Plan..? Or perhaps ActivePlan value should just be a part of Category..?
    val onImportTransactionMatcher: TransactionMatcher? = null,
    val isRememberedByDefault: Boolean = true,
) : ICategorizer, Parcelable {
    override fun categorize(transaction: Transaction): Transaction {
        return transaction.categorize(this)
    }

    override fun toString() = name // for logs

    @delegate:Ignore
    val displayType by lazy {
        when {
            this == DEFAULT || this == UNRECOGNIZED ->
                CategoryDisplayType.Special
            this.resetStrategy is ResetStrategy.Basic && (this.resetStrategy.budgetedMax?.isZero ?: false) ->
                CategoryDisplayType.Always
            else ->
                CategoryDisplayType.Reservoir
        }
    }

    companion object {
        val DEFAULT = Category("Default", AmountFormula.Value(BigDecimal.ZERO), true)
        val UNRECOGNIZED = Category("Unrecognized", AmountFormula.Value(BigDecimal.ZERO), true)
    }
}

fun Category.withDisplayType(categoryDisplayType: CategoryDisplayType): Category {
    return when (categoryDisplayType) {
        CategoryDisplayType.Special ->
            error("Unhandled type:$categoryDisplayType")
        CategoryDisplayType.Always ->
            this.copy(resetStrategy = ResetStrategy.Basic(budgetedMax = BigDecimal.ZERO))
        CategoryDisplayType.Reservoir ->
            this.copy(resetStrategy = ResetStrategy.Basic(budgetedMax = null))
    }
}