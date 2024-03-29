package com.tminus1010.buva.ui.review.pie_chart

import android.content.Context
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.tminus1010.buva.all_layers.extensions.isZero
import com.tminus1010.buva.all_layers.extensions.onNext
import com.tminus1010.buva.all_layers.extensions.throttleFist
import com.tminus1010.buva.all_layers.extensions.value
import com.tminus1010.buva.app.TransactionsInteractor
import com.tminus1010.buva.data.SelectableDurationRepo
import com.tminus1010.buva.data.UsePeriodTypeRepo
import com.tminus1010.buva.domain.*
import com.tminus1010.buva.ui.all_features.ColorSet
import com.tminus1010.buva.ui.all_features.view_model_item.PieChartVMItem
import com.tminus1010.buva.ui.all_features.view_model_item.SpinnerVMItem
import com.tminus1010.tmcommonkotlin.androidx.ShowToast
import com.tminus1010.tmcommonkotlin.coroutines.extensions.divertErrors
import com.tminus1010.tmcommonkotlin.coroutines.extensions.observe
import com.tminus1010.tmcommonkotlin.coroutines.extensions.pairwiseStartNull
import com.tminus1010.tmcommonkotlin.misc.extensions.sum
import com.tminus1010.tmcommonkotlin.tuple.tuple
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.math.BigDecimal
import javax.inject.Inject

@Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
@HiltViewModel
class ReviewPieChartVM @Inject constructor(
    transactionsInteractor: TransactionsInteractor,
    showToast: ShowToast,
    usePeriodTypeRepo: UsePeriodTypeRepo,
    selectableDurationRepo: SelectableDurationRepo,
) : ViewModel() {
    // # UserIntents
    val userPrevious = MutableSharedFlow<Unit>()
    val userNext = MutableSharedFlow<Unit>()
    val userClickDot = MutableSharedFlow<Int>()

    // # Private
    private val errors = MutableSharedFlow<Throwable>()
    private val currentPageNumber =
        selectableDurationRepo.flow.flatMapLatest {
            combine(periods, userClickDot)
            { periods, userClickDot ->
                ((periods.size - 1) - userClickDot).toLong()
            }.onStart { emit(0) }
                .flatMapLatest { i ->
                    merge(userPrevious.map { 1 }, userNext.map { -1 }, errors.filter { it is TooFarBackException }.map { -1 }) // TODO: Refactor
                        .scan(i) { acc, v ->
                            if (acc + v < 0) errors.onNext(NoMoreDataException())
                            (acc + v).coerceAtLeast(0)
                        }
                }
        }
            .distinctUntilChanged()
            .shareIn(viewModelScope, SharingStarted.Lazily, 1)

    private val period =
        combine(selectableDurationRepo.flow, currentPageNumber, usePeriodTypeRepo.flow, transactionsInteractor.transactionsAggregate.map { it.mostRecentSpend })
        { userSelectedDuration, currentPageNumber, userUsePeriodType, mostRecentSpend ->
            val mostRecentSpendDate = mostRecentSpend?.date ?: throw NoMostRecentSpendException()
            DatePeriodUtil.getPeriod(userSelectedDuration, userUsePeriodType, mostRecentSpendDate, currentPageNumber)
        }
            .pairwiseStartNull()
            .map { (a, b) ->
                if (b != null && b.endDate < transactionsInteractor.transactionsAggregate.value?.oldestSpend?.date)
                    a.also { errors.onNext(TooFarBackException()) }
                else
                    b
            }
            .divertErrors(errors)
            .shareIn(viewModelScope, SharingStarted.Lazily, 1)

    private val periods =
        combine(selectableDurationRepo.flow, usePeriodTypeRepo.flow, transactionsInteractor.transactionsAggregate)
        { userSelectedDuration, userUsePeriodType, transactionsAggregate ->
            val startDate = transactionsAggregate.oldestSpend?.date ?: return@combine listOf()
            val endDate = transactionsAggregate.mostRecentSpend?.date ?: return@combine listOf()
            val datePeriod = LocalDatePeriod(startDate, endDate)
            DatePeriodUtil.getPeriods(userSelectedDuration, userUsePeriodType, datePeriod)
        }
            .shareIn(viewModelScope, SharingStarted.Eagerly, 1)

    private val transactionBlock =
        combine(transactionsInteractor.transactionsAggregate, period)
        { transactionsAggregate, period ->
            TransactionBlock(
                period,
                transactionsAggregate.spends,
                false, // TODO: isFullyImported isn't important..? So just using false here..?
            )
        }

    companion object {
        private val otherColor = ColorSet.next()
    }

    /**
     * A [PieEntry] represents 1 chunk of the pie, but without everything it needs, like color.
     */
    private val pieEntriesAndColors =
        transactionBlock.map { transactionBlock ->
            val categoryAmountsRedefined =
                CategoryAmounts(transactionBlock.categoryAmounts.plus(tuple(Category("Uncategorized"), transactionBlock.defaultAmount)))
            listOfNotNull(
                *categoryAmountsRedefined.filter { it.value.abs() >= transactionBlock.total.abs() * BigDecimal(0.03) }
                    .map { tuple(it.key.name, tuple(PieEntry(it.value.abs().toFloat(), it.key.name), it.key.color)) }.toTypedArray(),
                categoryAmountsRedefined.filter { it.value.abs() < transactionBlock.total.abs() * BigDecimal(0.03) }
                    .let { if (it.values.sum().isZero) null else tuple("Other", tuple(PieEntry(it.values.sum().abs().toFloat(), "Other"), otherColor)) },
            )
                .sortedBy { it.first }
                .map { it.second }
        }

    /**
     * [PieDataSet] is a list of [PieEntry], combined with other information relevant to the entire list, like colors.
     */
    private val pieDataSet =
        pieEntriesAndColors.map { pieEntriesAndColors ->
            val pieEntries = pieEntriesAndColors.map { it.first }
            val colors = pieEntriesAndColors.map { it.second }
            PieDataSet(pieEntries, null).apply {
                setDrawValues(false)
                sliceSpace = 1f
                selectionShift = 2f
                this.colors = colors
            }
        }

    /**
     * [PieData] is the bare minimum to produce a [PieChart], but it is missing a lot of attributes, like centerText, entryLabelColor, isLegendEnabled.
     * It can contain and define shared data for multiple [PieDataSet], but usually there is only 1 [PieDataSet].
     */
    private val pieData = pieDataSet.map(::PieData)

    init {
        errors.observe(viewModelScope) {
            when (it) {
                is NoMostRecentSpendException -> logz("Swallowing error:${it.javaClass.simpleName}")
                is NoMoreDataException,
                is TooFarBackException,
                -> Unit

                else -> logz("error:", it)
            }
        }
    }

    // # Events
    init {
        errors
            .throttleFist(2000)
            .observe(viewModelScope) {
                when (it) {
                    is NoMostRecentSpendException -> Unit
                    is NoMoreDataException -> showToast("No more data. Import more transactions")
                    is TooFarBackException -> showToast("No more data")
                    else -> showToast("An error occurred")
                }
            }
    }

    // # State
    /**
     * [PieChartVMItem] is everything you need to produce a [PieChart], once you get access to a [Context] in the layer above.
     */
    val pieChartVMItem =
        PieChartVMItem(pieData.divertErrors(errors))
    val selectableDurationSpinnerVMItem =
        SpinnerVMItem(SelectableDuration.values(), selectableDurationRepo.flow, selectableDurationRepo::set)
    val usePeriodTypeSpinnerVMItem =
        SpinnerVMItem(UsePeriodType.values(), usePeriodTypeRepo.flow, usePeriodTypeRepo::set)
    val title =
        period.map { it?.toDisplayStr() ?: "Forever" }
    val leftVisibility =
        selectableDurationRepo.flow.map { if (it != SelectableDuration.FOREVER) View.VISIBLE else View.GONE }
    val isRightVisible =
        selectableDurationRepo.flow.map { if (it != SelectableDuration.FOREVER) View.VISIBLE else View.GONE }
    val dotCount =
        periods.map { it.count() }
    val selectedDot =
        combine(periods, period)
        { periods, period ->
            periods.indexOf(period)
        }
            .filterNot { it == -1 }
}