package com.tminus1010.buva.environment.adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import com.tminus1010.buva.all_layers.extensions.easyFromJson
import com.tminus1010.buva.all_layers.extensions.easyToJson
import com.tminus1010.buva.domain.*
import com.tminus1010.buva.environment.adapter.MoshiProvider.moshi
import com.tminus1010.tmcommonkotlin.misc.extensions.fromJson
import com.tminus1010.tmcommonkotlin.misc.extensions.toJson
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object MiscAdapter {
    /**
     * [CategoryDisplayType]
     */
    @ToJson
    fun toJson(x: CategoryDisplayType): String =
        x.ordinal.toString()

    @FromJson
    fun fromJson2(s: String): CategoryDisplayType =
        CategoryDisplayType.values()[s.toInt()]


    /**
     * [TerminationStrategy]
     */
    @ToJson
    fun toJson(x: TerminationStrategy): String =
        x.ordinal.toString()

    @FromJson
    fun fromJson3(s: String): TerminationStrategy =
        TerminationStrategy.values()[s.toInt()]


    /**
     * [TransactionMatcher]
     */
    @ToJson
    fun toJson(x: TransactionMatcher): String =
        when (x) {
            is TransactionMatcher.SearchText -> TransactionMatcher.SearchText.ordinal.toString() + "`" + x.searchText
            is TransactionMatcher.ByValue -> TransactionMatcher.ByValue.ordinal.toString() + "`" + x.searchTotal.toString()
            is TransactionMatcher.Multi -> TransactionMatcher.Multi.ordinal.toString() + "`" + moshi.easyToJson(x.transactionMatchers.map { toJson(it) })
        }

    @FromJson
    fun fromJson11(s: String): TransactionMatcher =
        when (s.takeWhile { it != '`' }.toInt()) {
            TransactionMatcher.SearchText.ordinal ->
                TransactionMatcher.SearchText(s.dropWhile { it != '`' }.drop(1))
            TransactionMatcher.ByValue.ordinal ->
                TransactionMatcher.ByValue(moshi.fromJson(s.dropWhile { it != '`' }.drop(1)))
            TransactionMatcher.Multi.ordinal ->
                TransactionMatcher.Multi(moshi.easyFromJson<List<String>>(s.dropWhile { it != '`' }.drop(1))!!.map { fromJson11(it) })
            else -> error("Unhandled s:$s")
        }


    /**
     * [ReconciliationStrategyGroup]
     */
    @ToJson
    fun toJson(x: ReconciliationStrategyGroup): String =
        when (x) {
            is ReconciliationStrategyGroup.Always -> "Always"
            is ReconciliationStrategyGroup.Reservoir ->
                moshi.toJson(x.resetStrategy)
        }

    @FromJson
    fun fromJson421(s: String): ReconciliationStrategyGroup =
        when (s) {
            "Always",
            -> ReconciliationStrategyGroup.Always
            else ->
                runCatching { ReconciliationStrategyGroup.Reservoir(moshi.fromJson<ResetStrategy?>(s)) }
                    .getOrElse { ReconciliationStrategyGroup.Reservoir(null) }
        }


    /**
     * [LocalDate]
     */
    val dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")

    @ToJson
    fun toJson(x: LocalDate?): String? =
        x?.format(dateFormatter)

    @FromJson
    fun fromJson4(s: String): LocalDate =
        s.let { LocalDate.parse(s, dateFormatter) }

    /**
     * [AmountFormula]
     */
    @ToJson
    fun toJson(x: AmountFormula): String =
        when (x) {
            is AmountFormula.Percentage -> "${x.percentage}:Percentage"
            is AmountFormula.Value -> "${x.amount}:Value"
        }

    @FromJson
    fun fromJson5(s: String): AmountFormula? =
        s.split(":")
            .let {
                when (it[1]) {
                    "Value" -> AmountFormula.Value(it[0].toBigDecimal())
                    "Percentage" -> AmountFormula.Percentage(it[0].toBigDecimal())
                    else -> error("Unhandled string")
                }
            }

    /**
     * Pair<[String], [BigDecimal]>
     */
    @ToJson
    fun toJson(x: @JvmSuppressWildcards Pair<String, BigDecimal>): String {
        return "${x.first}`${x.second}"
    }

    @FromJson
    fun fromJson7(s: String): @JvmSuppressWildcards Pair<String, BigDecimal> {
        return Pair(s.split("`")[0], BigDecimal(s.split("`")[1]))
    }
}