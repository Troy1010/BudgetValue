package com.tminus1010.buva.domain

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

@Parcelize
@Entity
data class Category(
    @PrimaryKey
    val name: String,
    val type: CategoryType = CategoryType.Always,
    val defaultAmountFormula: AmountFormula = AmountFormula.Value(BigDecimal.ZERO),
    val isRequired: Boolean = false,
    val onImportTransactionMatcher: TransactionMatcher? = null,
    val isRememberedByDefault: Boolean = true,
) : ICategorizer, Parcelable {
    override fun categorize(transaction: Transaction): Transaction {
        return transaction.categorize(this)
    }

    override fun toString() = name // for logs

    companion object {
        val DEFAULT = Category("Default", CategoryType.Special, AmountFormula.Value(BigDecimal.ZERO), true, null)
        val UNRECOGNIZED = Category("Unrecognized", CategoryType.Special, AmountFormula.Value(BigDecimal.ZERO), true, null)
    }
}