package com.tminus1010.budgetvalue.data.service

import com.squareup.moshi.FromJson
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.tminus1010.budgetvalue.domain.AmountFormula
import com.tminus1010.budgetvalue.domain.CategoryType
import com.tminus1010.budgetvalue.domain.TerminationStrategy
import com.tminus1010.budgetvalue.domain.TransactionMatcher
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
     * [CategoryType]
     */
    @ToJson
    fun toJson(x: CategoryType): String =
        x.ordinal.toString()

    @FromJson
    fun fromJson2(s: String): CategoryType =
        CategoryType.values()[s.toInt()]


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
            is TransactionMatcher.Multiple ->
                TransactionMatcher.Multiple.ordinal.toString() + "`" +
                        x.transactionMatchers.map { toJson(it) }
                            .let { basicMoshi.adapter<List<String>>(Types.newParameterizedType(List::class.java, String::class.java)).toJson(it)!! }
        }

    @FromJson
    fun fromJson11(s: String): TransactionMatcher =
        when (s.takeWhile { it != '`' }.toInt()) {
            TransactionMatcher.SearchText.ordinal ->
                TransactionMatcher.SearchText(
                    s.dropWhile { it != '`' }.drop(1)
                )
            TransactionMatcher.ByValue.ordinal ->
                TransactionMatcher.ByValue(
                    fromJson1(s.dropLastWhile { it != '`' }.drop(1))
                )
            TransactionMatcher.Multiple.ordinal ->
                TransactionMatcher.Multiple(
                    s.dropWhile { it != '`' }.drop(1)
                        .let {
                            basicMoshi.adapter<List<String>>(Types.newParameterizedType(List::class.java, String::class.java))
                                .fromJson(it)!!
                                .map { fromJson11(it) }
                        }
                )
            else -> error("Unhandled s:$s")
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

    @ToJson
    fun toJson(x: AmountFormula): String =
        x.toDTO()

    @FromJson
    fun fromJson5(s: String): AmountFormula? =
        AmountFormula.fromDTO(s)

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
}