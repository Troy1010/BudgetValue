package com.tminus1010.buva.domain

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.tminus1010.tmcommonkotlin.tuple.createTuple
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
    val reconciliationStrategyGroup: ReconciliationStrategyGroup = ReconciliationStrategyGroup.Reservoir(ResetStrategy.Basic(0)),
    val onImportTransactionMatcher: TransactionMatcher? = null,
    val isRememberedWithEditByDefault: Boolean = true,
) : ICategorizer, Parcelable {
    override fun categorize(transaction: Transaction): Transaction {
        return transaction.categorize(this)
    }

    override fun toString() = createTuple(name, "ReconciliationStrategyGroup.${reconciliationStrategyGroup::class.java.simpleName}", (reconciliationStrategyGroup.resetStrategy as? ResetStrategy.Basic)?.budgetedMax).toString() // for logs

    @delegate:Ignore
    val displayType by lazy {
        when {
            this == DEFAULT || this == UNRECOGNIZED ->
                CategoryDisplayType.Special
            reconciliationStrategyGroup is ReconciliationStrategyGroup.Always ->
                CategoryDisplayType.Always
            reconciliationStrategyGroup is ReconciliationStrategyGroup.Unlimited ->
                CategoryDisplayType.Unlimited
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

// TODO: Why is this an extension..?
fun Category.withDisplayType(categoryDisplayType: CategoryDisplayType): Category {
    return when (categoryDisplayType) {
        CategoryDisplayType.Special ->
            error("Unhandled type:$categoryDisplayType")
        CategoryDisplayType.Unlimited ->
            this.copy(reconciliationStrategyGroup = ReconciliationStrategyGroup.Unlimited)
        CategoryDisplayType.Always ->
            this.copy(reconciliationStrategyGroup = ReconciliationStrategyGroup.Always)
        CategoryDisplayType.Reservoir ->
            this.copy(reconciliationStrategyGroup = ReconciliationStrategyGroup.Reservoir())
    }
}