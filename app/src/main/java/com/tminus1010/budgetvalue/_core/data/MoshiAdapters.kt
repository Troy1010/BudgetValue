package com.tminus1010.budgetvalue._core.data

import com.squareup.moshi.FromJson
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.tminus1010.budgetvalue.categories.models.CategoryType
import com.tminus1010.budgetvalue.replay_or_future.models.TerminationStatus
import com.tminus1010.tmcommonkotlin.misc.extensions.fromJson
import com.tminus1010.tmcommonkotlin.misc.extensions.toJson
import java.math.BigDecimal

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
                    basicMoshi.toJson(x.terminationDate)
                else ""
            )

    @FromJson
    fun fromJson3(s: String): TerminationStatus =
        when (s.takeWhile { it != '`' }.toLong()) {
            TerminationStatus.TERMINATED.ordinal ->
                TerminationStatus.TERMINATED(
                    terminationDate = basicMoshi.fromJson(s.dropWhile { it != '`' }.drop(1))
                )
            TerminationStatus.PERMANENT.ordinal ->
                TerminationStatus.PERMANENT
            TerminationStatus.WAITING_FOR_MATCH.ordinal ->
                TerminationStatus.WAITING_FOR_MATCH
            else -> error("Unrecognized ordinal:${s.takeWhile { it != '`' }.toLong()}")
        }
}