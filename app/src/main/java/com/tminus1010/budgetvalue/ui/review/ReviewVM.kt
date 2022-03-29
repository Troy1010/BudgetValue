package com.tminus1010.budgetvalue.ui.review

import android.content.Context
import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.tminus1010.budgetvalue._unrestructured.transactions.app.TransactionBlock
import com.tminus1010.budgetvalue.domain.TransactionsAggregate
import com.tminus1010.budgetvalue.data.TransactionsRepo
import com.tminus1010.budgetvalue.all_layers.extensions.asObservable2
import com.tminus1010.budgetvalue.all_layers.extensions.divertErrors
import com.tminus1010.budgetvalue.all_layers.extensions.value
import com.tminus1010.budgetvalue.domain.Category
import com.tminus1010.budgetvalue.domain.CategoryAmounts
import com.tminus1010.budgetvalue.domain.LocalDatePeriod
import com.tminus1010.budgetvalue.ui.all_features.model.PieChartVMItem
import com.tminus1010.budgetvalue.ui.all_features.model.SpinnerVMItem
import com.tminus1010.budgetvalue.ui.review.presentation.NoMoreDataException
import com.tminus1010.budgetvalue.ui.review.presentation.NoMostRecentSpendException
import com.tminus1010.budgetvalue.ui.review.presentation.TooFarBackException
import com.tminus1010.tmcommonkotlin.core.extensions.nextOrSame
import com.tminus1010.tmcommonkotlin.core.extensions.previousOrSame
import com.tminus1010.tmcommonkotlin.misc.extensions.sum
import com.tminus1010.tmcommonkotlin.rx.extensions.pairwise
import com.tminus1010.tmcommonkotlin.rx.replayNonError
import com.tminus1010.tmcommonkotlin.tuple.Box
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
@HiltViewModel
class ReviewVM @Inject constructor(
    transactionsRepo: TransactionsRepo,
) : ViewModel() {
    // # Events
    val errors = PublishSubject.create<Throwable>()

    // # UserIntents
    val userSelectedDuration = BehaviorSubject.createDefault(SelectableDuration.BY_MONTH)
    val userUsePeriodType = BehaviorSubject.createDefault(UsePeriodType.USE_DAY_COUNT_PERIODS)
    val userPrevious = PublishSubject.create<Unit>()
    val userNext = PublishSubject.create<Unit>()

    // # Internal
    private val currentPageNumber =
        userSelectedDuration.switchMap {
            Observable.merge(userPrevious.map { 1 }, userNext.map { -1 }, errors.filter { it is TooFarBackException }.map { -1 })
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
        Observable.combineLatest(userSelectedDuration, currentPageNumber, userUsePeriodType, transactionsRepo.transactionsAggregate2.map { it.mostRecentSpend }.asObservable2()) // TODO("Filtering for not-null on mostRecentSpend might be bad?")
        { userSelectedDuration, currentPageNumber, userUsePeriodType, mostRecentSpend ->
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
            .startWithItem(Box(null))
            .pairwise()
            .map { (a, b) ->
                if (b.first != null && b.first!!.endDate < transactionsRepo.transactionsAggregate2.value?.oldestSpend?.date)
                    a.also { errors.onNext(TooFarBackException()) }
                else
                    b
            }
            .divertErrors(errors)
            .replayNonError(1)

    private val transactionBlock =
        Observable.combineLatest(
            transactionsRepo.transactionsAggregate2.asObservable2().map(TransactionsAggregate::spends),
            period,
            ::TransactionBlock,
        )
            .throttleLatest(50, TimeUnit.MILLISECONDS)

    /**
     * A [PieEntry] represents 1 chunk of the pie, but without everything it needs, like color.
     */
    private val pieEntries =
        transactionBlock.map { transactionBlock ->
            val categoryAmountsRedefined =
                CategoryAmounts(transactionBlock.categoryAmounts.plus(Category("Uncategorized") to transactionBlock.defaultAmount))
            listOfNotNull(
                *categoryAmountsRedefined.filter { it.value.abs() >= transactionBlock.amount.abs() * BigDecimal(0.03) }
                    .map { it.value.abs() to PieEntry(it.value.abs().toFloat(), it.key.name) }.toTypedArray(),
                categoryAmountsRedefined.filter { it.value.abs() < transactionBlock.amount.abs() * BigDecimal(0.03) }
                    .let { if (it.isEmpty()) null else it.values.sum().abs() to PieEntry(it.values.sum().abs().toFloat(), "Other") },
            )
                .sortedBy { it.first }
                .map { it.second }
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

    // # State
    /**
     * [PieChartVMItem] is everything you need to produce a [PieChart], once you get access to a [Context] in the layer above.
     */
    val pieChartVMItem =
        PieChartVMItem(
            pieData = pieData.divertErrors(errors),
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