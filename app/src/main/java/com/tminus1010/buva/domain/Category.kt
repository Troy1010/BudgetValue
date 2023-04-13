package com.tminus1010.buva.domain

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
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
    val reconciliationStrategyGroup: ReconciliationStrategyGroup = ReconciliationStrategyGroup.Always,
    val onImportTransactionMatcher: TransactionMatcher? = null,
    val isRememberedByDefault: Boolean = true,
) : ICategorizer, Parcelable {
    override fun categorize(transaction: Transaction): Transaction {
        return transaction.categorize(this)
    }

    override fun toString() = Pair(name, (reconciliationStrategyGroup.resetStrategy as? ResetStrategy.Basic)?.budgetedMax).toString() // for logs

    @delegate:Ignore
    val displayType by lazy {
        when {
            this == DEFAULT || this == UNRECOGNIZED ->
                CategoryDisplayType.Special
            reconciliationStrategyGroup is ReconciliationStrategyGroup.Always ->
                CategoryDisplayType.Always
            else ->
                CategoryDisplayType.Reservoir
        }
    }

    override fun equals(other: Any?): Boolean {
        return (name == (other as? Category)?.name)
                && reconciliationStrategyGroup == (other as? Category)?.reconciliationStrategyGroup
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + reconciliationStrategyGroup.hashCode()
        return result
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
            this.copy(reconciliationStrategyGroup = ReconciliationStrategyGroup.Always)
        CategoryDisplayType.Reservoir ->
            this.copy(reconciliationStrategyGroup = ReconciliationStrategyGroup.Reservoir(ResetStrategy.Basic(null)))
    }
}