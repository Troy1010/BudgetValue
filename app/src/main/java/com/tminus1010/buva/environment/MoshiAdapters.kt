package com.tminus1010.buva.environment

import com.squareup.moshi.FromJson
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.tminus1010.buva.domain.*
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object MoshiAdapters {
    val basicMoshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

    /**
     * [BigDecimal]
     */
    @ToJson
    fun toJson(x: BigDecimal): String =
        x.toString()

    @FromJson
    fun fromJson1(s: String): BigDecimal =
        s.toBigDecimal()

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
            is TransactionMatcher.Multi -> TransactionMatcher.Multi.ordinal.toString() + "`" + toJson(x.transactionMatchers.map { toJson(it) })
        }

    @FromJson
    fun fromJson11(s: String): TransactionMatcher =
        when (s.takeWhile { it != '`' }.toInt()) {
            TransactionMatcher.SearchText.ordinal ->
                TransactionMatcher.SearchText(s.dropWhile { it != '`' }.drop(1))
            TransactionMatcher.ByValue.ordinal ->
                TransactionMatcher.ByValue(fromJson1(s.dropWhile { it != '`' }.drop(1)))
            TransactionMatcher.Multi.ordinal ->
                TransactionMatcher.Multi(fromJson6(s.dropWhile { it != '`' }.drop(1))!!.map { fromJson11(it) })
            else -> error("Unhandled s:$s")
        }


    /**
     * [ResetStrategy]
     */
    @ToJson
    fun toJson(x: ResetStrategy): String =
        when (x) {
            is ResetStrategy.Basic -> x.budgetedMax.toString()
        }

    @FromJson
    fun fromJson20(s: String): ResetStrategy =
        if (s == "null")
            ResetStrategy.Basic(null)
        else
            ResetStrategy.Basic(runCatching { s.toBigDecimal() }.getOrDefault(BigDecimal.ZERO))


    /**
     * [ResolutionStrategy]
     */
    @ToJson
    fun toJson(x: ResolutionStrategy): String =
        when (x) {
            is ResolutionStrategy.Basic -> x.budgetedMin.toString()
            is ResolutionStrategy.MatchPlan -> "MatchPlan"
        }

    @FromJson
    fun fromJson21(s: String): ResolutionStrategy =
        when (s) {
            "null" -> ResolutionStrategy.Basic(null)
            "MatchPlan" -> ResolutionStrategy.MatchPlan
            else -> ResolutionStrategy.Basic(runCatching { s.toBigDecimal() }.getOrDefault(BigDecimal.ZERO))
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
     * List<[String]>
     */
    @ToJson
    fun toJson(x: List<String>): String {
        val type = Types.newParameterizedType(List::class.java, String::class.java)
        return basicMoshi.adapter<List<String>>(type).toJson(x)
    }

    @FromJson
    fun fromJson6(s: String): List<String>? {
        val type = Types.newParameterizedType(List::class.java, String::class.java)
        return basicMoshi.adapter<List<String>>(type).fromJson(s)
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