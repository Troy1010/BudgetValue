package com.tminus1010.buva.environment.adapter

import com.tminus1010.buva.environment.adapter.MoshiProvider.moshi
import com.tminus1010.tmcommonkotlin.misc.extensions.fromJson
import com.tminus1010.tmcommonkotlin.misc.extensions.toJson
import com.tminus1010.tmcommonkotlin.tuple.createTuple
import junit.framework.Assert.assertEquals
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import java.time.LocalDate

internal class LocalDateAdapterTest {
    @TestFactory
    fun test(): List<DynamicTest> {
        return listOf(
            createTuple(LocalDate.parse("2022-01-01")),
            createTuple<LocalDate?>(null),
        )
            .map { (givenLocalDate) ->
                DynamicTest.dynamicTest("Given localDate:$givenLocalDate") {
                    // # When
                    val actual =
                        moshi.toJson<LocalDate?>(givenLocalDate)
                            .let { moshi.fromJson<LocalDate?>(it) }
                    // # Then
                    assertEquals(actual, givenLocalDate)
                }
            }
    }
}