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
import com.tminus1010.budgetvalue.all.presentation_and_view.UsePeriodType
import com.tminus1010.budgetvalue.all.presentation_and_view._models.NoMoreDataException
import com.tminus1010.budgetvalue.all.presentation_and_view._models.NoMostRecentSpendException
import com.tminus1010.budgetvalue.all.presentation_and_view._models.PieChartVMItem
import com.tminus1010.budgetvalue.all.presentation_and_view._models.SpinnerVMItem
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.transactions.data.TransactionsRepo
import com.tminus1010.budgetvalue.transactions.domain.models.TransactionsAggregate
import com.tminus1010.tmcommonkotlin.core.extensions.nextOrSame
import com.tminus1010.tmcommonkotlin.core.extensions.previousOrSame
import com.tminus1010.tmcommonkotlin.misc.extensions.sum
import com.tminus1010.tmcommonkotlin.rx.replayNonError
import com.tminus1010.tmcommonkotlin.tuple.Box
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.math.BigDecimal
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import javax.inject.Inject

@Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
@HiltViewModel
class ReviewVM @Inject constructor(
    transactionsRepo: TransactionsRepo,
) : ViewModel() {
    // # UserIntents
    val userSelectedDuration = BehaviorSubject.createDefault(SelectableDuration.BY_MONTH)
    val userUsePeriodType = BehaviorSubject.createDefault(UsePeriodType.USE_DAY_COUNT_PERIODS)
    val userPrevious = PublishSubject.create<Unit>()
    val userNext = PublishSubject.create<Unit>()

    // # Internal
    private val currentPageNumber =
        userSelectedDuration.switchMap {
            Observable.merge(userPrevious.map { 1 }, userNext.map { -1 })
                .scan(0) { acc, v ->
                    if (acc + v < 0) errors.onNext(NoMoreDataException())
                    (acc + v).coerceAtLeast(0)
                }
                .map(Int::toLong)
        }
    private val _colors = listOf<Int>()
        .plus(ColorTemplate.VORDIPLOM_COLORS.toList())
        .plus(ColorTemplate.JOYFUL_COLORS.toList())
        .plus(ColorTemplate.COLORFUL_COLORS.toList())
        .plus(ColorTemplate.PASTEL_COLORS.toList())
    private val period =
        Observable.combineLatest(userSelectedDuration, currentPageNumber, userUsePeriodType, transactionsRepo.transactionsAggregate.mapBox { it.mostRecentSpend }.filter { it.first != null }) // TODO("Filtering for not-null seems like a duct-tape solution b/c error stops subscription")
        { userSelectedDuration, currentPageNumber, userUsePeriodType, (mostRecentSpend) ->
            val mostRecentSpendDate = (mostRecentSpend?.date ?: throw NoMostRecentSpendException())
            when (userSelectedDuration) {
                SelectableDuration.BY_WEEK ->
                    when (userUsePeriodType) {
                        UsePeriodType.USE_DAY_COUNT_PERIODS ->
                            LocalDatePeriod(
                                mostRecentSpendDate.minusDays((currentPageNumber + 1) * 7),
                                mostRecentSpendDate.minusDays((currentPageNumber) * 7),
                            )
                        UsePeriodType.USE_CALENDAR_PERIODS -> {
                            val dateToConsider = mostRecentSpendDate.minusWeeks(currentPageNumber)
                            LocalDatePeriod(
                                dateToConsider.previousOrSame(DayOfWeek.SUNDAY),
                                dateToConsider.nextOrSame(DayOfWeek.SATURDAY),
                            )
                        }
                    }
                SelectableDuration.BY_MONTH ->
                    when (userUsePeriodType) {
                        UsePeriodType.USE_DAY_COUNT_PERIODS ->
                            LocalDatePeriod(
                                mostRecentSpendDate.minusDays((currentPageNumber + 1) * 30),
                                mostRecentSpendDate.minusDays((currentPageNumber) * 30),
                            )
                        UsePeriodType.USE_CALENDAR_PERIODS -> {
                            val dateToConsider = mostRecentSpendDate.minusMonths(currentPageNumber)
                            LocalDatePeriod(
                                dateToConsider.withDayOfMonth(1),
                                dateToConsider.withDayOfMonth(dateToConsider.lengthOfMonth()),
                            )
                        }
                    }
                SelectableDuration.BY_3_MONTHS ->
                    when (userUsePeriodType) {
                        UsePeriodType.USE_DAY_COUNT_PERIODS ->
                            LocalDatePeriod(
                                mostRecentSpendDate.minusDays((currentPageNumber + 1) * 365 / 4),
                                mostRecentSpendDate.minusDays((currentPageNumber) * 365 / 4),
                            )
                        UsePeriodType.USE_CALENDAR_PERIODS -> {
                            val dateToConsider = mostRecentSpendDate.minusMonths(currentPageNumber * 3)
                            val firstQuarter =
                                LocalDatePeriod(
                                    LocalDate.of(dateToConsider.year, Month.JANUARY, 1),
                                    LocalDate.of(dateToConsider.year, Month.MARCH, Month.MARCH.length(dateToConsider.isLeapYear)),
                                )
                            val secondQuarter =
                                LocalDatePeriod(
                                    LocalDate.of(dateToConsider.year, Month.APRIL, 1),
                                    LocalDate.of(dateToConsider.year, Month.JUNE, Month.JUNE.length(dateToConsider.isLeapYear)),
                                )
                            val thirdQuarter =
                                LocalDatePeriod(
                                    LocalDate.of(dateToConsider.year, Month.JULY, 1),
                                    LocalDate.of(dateToConsider.year, Month.SEPTEMBER, Month.SEPTEMBER.length(dateToConsider.isLeapYear)),
                                )
                            val fourthQuarter =
                                LocalDatePeriod(
                                    LocalDate.of(dateToConsider.year, Month.OCTOBER, 1),
                                    LocalDate.of(dateToConsider.year, Month.DECEMBER, Month.DECEMBER.length(dateToConsider.isLeapYear)),
                                )
                            when (dateToConsider) {
                                in firstQuarter -> firstQuarter
                                in secondQuarter -> secondQuarter
                                in thirdQuarter -> thirdQuarter
                                in fourthQuarter -> fourthQuarter
                                else -> error("dateToConsider:${dateToConsider} was somehow not in any quarters.")
                            }
                        }
                    }
                SelectableDuration.BY_6_MONTHS ->
                    when (userUsePeriodType) {
                        UsePeriodType.USE_DAY_COUNT_PERIODS ->
                            LocalDatePeriod(
                                mostRecentSpendDate.minusDays((currentPageNumber + 1) * 365 / 2),
                                mostRecentSpendDate.minusDays((currentPageNumber) * 365 / 2),
                            )
                        UsePeriodType.USE_CALENDAR_PERIODS -> {
                            val dateToConsider = mostRecentSpendDate.minusMonths(currentPageNumber * 6)
                            val firstHalfOfYear =
                                LocalDatePeriod(
                                    LocalDate.of(dateToConsider.year, Month.JANUARY, 1),
                                    LocalDate.of(dateToConsider.year, Month.JUNE, Month.JUNE.length(dateToConsider.isLeapYear)),
                                )
                            val secondHalfOfYear =
                                LocalDatePeriod(
                                    LocalDate.of(dateToConsider.year, Month.JULY, 1),
                                    LocalDate.of(dateToConsider.year, Month.DECEMBER, Month.DECEMBER.length(dateToConsider.isLeapYear)),
                                )
                            if (dateToConsider in firstHalfOfYear) firstHalfOfYear else secondHalfOfYear
                        }
                    }
                SelectableDuration.BY_YEAR ->
                    when (userUsePeriodType) {
                        UsePeriodType.USE_DAY_COUNT_PERIODS ->
                            LocalDatePeriod(
                                mostRecentSpendDate.minusDays((currentPageNumber + 1) * 365),
                                mostRecentSpendDate.minusDays((currentPageNumber) * 365),
                            )
                        UsePeriodType.USE_CALENDAR_PERIODS -> {
                            val dateToConsider = mostRecentSpendDate.minusYears(currentPageNumber)
                            LocalDatePeriod(
                                LocalDate.of(dateToConsider.year, Month.JANUARY, 1),
                                LocalDate.of(dateToConsider.year, Month.DECEMBER, Month.DECEMBER.length(dateToConsider.isLeapYear)),
                            )
                        }
                    }
                SelectableDuration.FOREVER ->
                    null
            }.let { Box(it) }
        }
            .replayNonError(1)

    private val transactionBlock =
        Observable.combineLatest(
            transactionsRepo.transactionsAggregate.map(TransactionsAggregate::transactions),
            period,
            ::TransactionBlock,
        )

    /**
     * A [PieEntry] represents 1 chunk of the pie, but without everything it needs, like color.
     */
    private val pieEntries =
        transactionBlock.map { transactionBlock ->
            val categoryAmounts = CategoryAmounts(transactionBlock.categoryAmounts.plus(Category("Uncategorized") to transactionBlock.defaultAmount))
            listOfNotNull(
                categoryAmounts.filter { it.value.abs() < transactionBlock.amount.abs() * BigDecimal(0.03) }
                    .let { if (it.isEmpty()) null else PieEntry(it.values.sum().abs().toFloat(), "Other") },
                *categoryAmounts.filter { it.value.abs() >= transactionBlock.amount.abs() * BigDecimal(0.03) }
                    .map { PieEntry(it.value.abs().toFloat(), it.key.name) }.toTypedArray()
            )
        }

    /**
     * [PieDataSet] is a list of [PieEntry], combined with other information relevant to the entire list, like colors.
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
     * [PieData] is the bare minimum to produce a [PieChart], but it is missing a lot of attributes, like centerText, entryLabelColor, isLegendEnabled.
     * It can contain and define shared data for multiple [PieDataSet], but usually there is only 1 [PieDataSet].
     */
    private val pieData = pieDataSet.map(::PieData)

    // # Events
    val errors = PublishSubject.create<Throwable>()

    // # State
    /**
     * [PieChartVMItem] is everything you need to produce a [PieChart], once you get access to a [Context] in the layer above.
     */
    val pieChartVMItem =
        PieChartVMItem(
            pieData.divertErrors(errors),
            centerText = "Spending"
        )

    val selectableDurationSpinnerVMItem =
        SpinnerVMItem(
            SelectableDuration.values(),
            userSelectedDuration,
        )

    val usePeriodTypeSpinnerVMItem =
        SpinnerVMItem(
            UsePeriodType.values(),
            userUsePeriodType,
        )

    val title =
        period.map { (it) -> it?.toDisplayStr() ?: "Forever" }
    val isLeftVisible =
        userSelectedDuration.map { it != SelectableDuration.FOREVER }
    val isRightVisible =
        userSelectedDuration.map { it != SelectableDuration.FOREVER }
}