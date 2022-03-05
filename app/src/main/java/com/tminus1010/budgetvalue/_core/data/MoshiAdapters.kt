package com.tminus1010.budgetvalue._core.data

import com.squareup.moshi.FromJson
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.tminus1010.budgetvalue.categories.models.CategoryType
import com.tminus1010.budgetvalue.replay_or_future.domain.TerminationStatus
import com.tminus1010.budgetvalue.transactions.app.AmountFormula
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
     * [TerminationStatus]
     */
    @ToJson
    fun toJson(x: TerminationStatus): String =
        x.ordinal.toString()
            .plus("`")
            .plus(
                if (x is TerminationStatus.TERMINATED)
                    toJson(x.terminationDate)
                else ""
            )

    @FromJson
    fun fromJson3(s: String): TerminationStatus =
        when (s.takeWhile { it != '`' }.toLong()) {
            TerminationStatus.TERMINATED.ordinal ->
                TerminationStatus.TERMINATED(
                    terminationDate = fromJson4(s.dropWhile { it != '`' }.drop(1))
                )
            TerminationStatus.PERMANENT.ordinal ->
                TerminationStatus.PERMANENT
            TerminationStatus.WAITING_FOR_MATCH.ordinal ->
                TerminationStatus.WAITING_FOR_MATCH
            else -> error("Unrecognized ordinal:${s.takeWhile { it != '`' }.toLong()}")
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