package com.tminus1010.budgetvalue.all.presentation_and_view.review

import android.content.Context
import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.tminus1010.budgetvalue._core.domain.LocalDatePeriod
import com.tminus1010.budgetvalue._core.extensions.divertErrors
import com.tminus1010.budgetvalue._core.extensions.mapBox
import com.tminus1010.budgetvalue._core.models.CategoryAmounts
import com.tminus1010.budgetvalue.all.domain.models.TransactionBlock
import com.tminus1010.budgetvalue.all.presentation_and_view.SelectableDuration
import com.tminus1010.budgetvalue.all.presentation_and_view._models.NoMostRecentSpend
import com.tminus1010.budgetvalue.all.presentation_and_view._models.PieChartVMItem
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.transactions.data.TransactionsRepo
import com.tminus1010.budgetvalue.transactions.domain.TransactionsAppService
import com.tminus1010.tmcommonkotlin.misc.extensions.sum
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.math.BigDecimal
import javax.inject.Inject

@Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
@HiltViewModel
class ReviewVM @Inject constructor(
    transactionsRepo: TransactionsRepo,
    transactionsAppService: TransactionsAppService,
) : ViewModel() {
    // # UserIntents
    val userSelectedDuration = BehaviorSubject.createDefault(SelectableDuration.THIS_MONTH)!!

    // # Internal
    private val _colors = listOf<Int>()
        .plus(ColorTemplate.VORDIPLOM_COLORS.toList())
        .plus(ColorTemplate.JOYFUL_COLORS.toList())
        .plus(ColorTemplate.COLORFUL_COLORS.toList())
        .plus(ColorTemplate.PASTEL_COLORS.toList())
    private val transactionBlock =
        Observable.combineLatest(userSelectedDuration, transactionsRepo.transactions, transactionsAppService.transactions2.mapBox { it.mostRecentSpend }.filter { it.first != null }) // TODO("Filtering for not-null seems like a duct-tape solution b/c error stops subscription")
        { userSelectedDuration, transactions, (mostRecentSpend) ->
            val period =
                when (userSelectedDuration) {
                    SelectableDuration.THIS_MONTH -> LocalDatePeriod(
                        (mostRecentSpend?.date ?: throw NoMostRecentSpend()).minusDays(30),
                        mostRecentSpend.date,
                    )
                    SelectableDuration.TWO_MONTHS_COMBINED ->
                        LocalDatePeriod(
                            (mostRecentSpend?.date ?: throw NoMostRecentSpend()).minusDays(60),
                            mostRecentSpend.date,
                        )
                    SelectableDuration.ONE_MONTH_AGO ->
                        LocalDatePeriod(
                            (mostRecentSpend?.date ?: throw NoMostRecentSpend()).minusDays(60),
                            mostRecentSpend.date.minusDays(30),
                        )
                    SelectableDuration.TWO_MONTHS_AGO ->
                        LocalDatePeriod(
                            (mostRecentSpend?.date ?: throw NoMostRecentSpend()).minusDays(90),
                            mostRecentSpend.date.minusDays(60),
                        )
                    SelectableDuration.FOREVER ->
                        null
                }
            TransactionBlock(transactions, period)
        }
            .observeOn(AndroidSchedulers.mainThread())

    /**
     * A [PieEntry] represents 1 chunk of the pie, but without everything it needs, like color.
     */
    private val pieEntries =
        transactionBlock
            .map { transactionBlock ->
                val categoryAmounts = CategoryAmounts(transactionBlock.categoryAmounts.plus(Category("Uncategorized") to transactionBlock.defaultAmount))
                listOfNotNull(
                    categoryAmounts.filter { it.value.abs() < transactionBlock.amount.abs() * BigDecimal(0.03) }
                        .let { if (it.isEmpty()) null else PieEntry(it.values.sum().abs().toFloat(), "Other") },
                    *categoryAmounts.filter { it.value.abs() >= transactionBlock.amount.abs() * BigDecimal(0.03) }
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

    // # State
    val errors = PublishSubject.create<Throwable>()!!

    /**
     * A [PieChartVMItem] is everything you need to produce a [PieChart], once you get access to a [Context] in the layer above.
     */
    val pieChartVMItem =
        PieChartVMItem(
            pieData.divertErrors(errors),
            centerText = "Spending"
        )

    val initialSelectedDuration = userSelectedDuration.value!!
}