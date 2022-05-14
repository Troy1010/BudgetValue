package com.tminus1010.buva.domain


import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tminus1010.buva.all_layers.extensions.copy
import com.tminus1010.tmcommonkotlin.misc.extensions.sum
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

/**
 * the [id] is the entire row of information for that transaction
 */
@Parcelize
@Entity
data class Transaction(
    val date: LocalDate,
    val description: String,
    val amount: BigDecimal,
    val categoryAmounts: CategoryAmounts,
    val categorizationDate: LocalDate?,
    @PrimaryKey
    val id: String,
) : Parcelable {
    val isUncategorized get() = categoryAmounts.isNullOrEmpty()
    val isCategorized get() = !isUncategorized
    val isSpend get() = amount < BigDecimal.ZERO
    val defaultAmount get() = amount - categoryAmounts.values.sum()
    fun categorize(categoryAmounts: CategoryAmounts): Transaction {
        return this.copy(
            categoryAmounts = categoryAmounts,
            categorizationDate = LocalDate.now()
        )
    }

    fun calcFillAmount(fillCategory: Category): BigDecimal {
        return categoryAmounts
            .filter { it.key != fillCategory }
            .let { categoryAmounts -> amount - categoryAmounts.values.sum() }
    }

    fun categorize(category: Category): Transaction {
        if (category == Category.DEFAULT) return this
        return categorize(
            CategoryAmounts(
                categoryAmounts
                    .filter { it.key != category }
                    .let { categoryAmounts ->
                        categoryAmounts
                            .copy(category to amount - categoryAmounts.values.sum())
                    }
            )
        )
    }

    fun calcCAsAdjustedForNewTotal(newTotal: BigDecimal): Map<Category, BigDecimal> {
        val dumpEverythingIntoLast = (defaultAmount.compareTo(BigDecimal.ZERO) == 0)
        var totalSoFar = BigDecimal.ZERO
        return (newTotal / amount)
            .let { ratio ->
                categoryAmounts.mapValues {
                    if (dumpEverythingIntoLast && it.key == categoryAmounts.keys.lastOrNull())
                        newTotal - totalSoFar
                    else
                        (it.value * ratio).setScale(2, RoundingMode.HALF_UP)
                            .also { totalSoFar += it }
                }
            }
    }
}