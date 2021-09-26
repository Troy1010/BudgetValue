package com.tminus1010.budgetvalue.all.presentation_and_view.review

import android.content.Context
import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.tminus1010.budgetvalue._core.models.CategoryAmounts
import com.tminus1010.budgetvalue.all.presentation_and_view._models.PieChartVMItem
import com.tminus1010.budgetvalue.transactions.data.TransactionsRepo
import com.tminus1010.tmcommonkotlin.misc.extensions.sum
import dagger.hilt.android.lifecycle.HiltViewModel
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class ReviewVM @Inject constructor(
    transactionsRepo: TransactionsRepo
) : ViewModel() {
    private val _colors = listOf<Int>()
        .plus(ColorTemplate.VORDIPLOM_COLORS.toList())
        .plus(ColorTemplate.JOYFUL_COLORS.toList())
        .plus(ColorTemplate.COLORFUL_COLORS.toList())
        .plus(ColorTemplate.PASTEL_COLORS.toList())
    private val categoryAmounts =
        transactionsRepo.transactions
            .map { it.fold(CategoryAmounts()) { acc, transaction -> acc.addTogether(transaction.categoryAmounts) } }

    /**
     * List of [PieEntry]. A [PieEntry] represents 1 chunk of the pie, but without everything it needs, like color.
     */
    private val pieEntries =
        categoryAmounts.map { categoryAmounts ->
            listOf(
                categoryAmounts.filter { it.value.abs() < categoryAmounts.categorizedAmount.abs() * BigDecimal(0.03) }
                    .let { PieEntry(it.values.sum().abs().toFloat(), "Other") },
                *categoryAmounts.filter { it.value.abs() >= categoryAmounts.categorizedAmount.abs() * BigDecimal(0.03) }
                    .map { PieEntry(it.value.abs().toFloat(), it.key.name) }.toTypedArray()
            )
        }

    /**
     * A [PieDataSet] is a list of [PieEntry], combined with other information relevant to the entire list, like colors.
     */
    private val pieDataSet =
        pieEntries.map { pieEntries ->
            PieDataSet(pieEntries, null).apply {
                setDrawValues(false)
                sliceSpace = 1f
                selectionShift = 2f
                colors = _colors
            }
        }

    /**
     * A [PieData] is the bare minimum to produce a [PieChart], but it is missing a lot of attributes, like centerText, entryLabelColor, isLegendEnabled.
     * It can contain and define shared data for multiple [PieDataSet], but usually there is only 1 [PieDataSet].
     */
    private val pieData = pieDataSet.map(::PieData)!!

    /**
     * A [PieChartVMItem] is everything you need to produce a [PieChart], once you get access to a [Context] in the layer above.
     */
    val pieChartVMItem =
        PieChartVMItem(
            pieData,
            centerText = "Spending"
        )
}