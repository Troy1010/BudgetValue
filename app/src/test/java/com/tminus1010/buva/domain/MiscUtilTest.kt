package com.tminus1010.buva.domain

import com.tminus1010.tmcommonkotlin.tuple.tuple
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import java.time.LocalDate

internal class MiscUtilTest {
    @TestFactory
    fun isPeriodFullyImported(): List<DynamicTest> {
        // # Given
        return listOf(
            tuple(
                LocalDatePeriod(startDate = LocalDate.parse("2023-04-05"), endDate = LocalDate.parse("2023-04-18")),
                listOf(
                    TransactionImportInfo(period = LocalDatePeriod(startDate = LocalDate.parse("2022-07-25"), endDate = LocalDate.parse("2022-09-22")), id = 1),
                    TransactionImportInfo(period = LocalDatePeriod(startDate = LocalDate.parse("2022-09-12"), endDate = LocalDate.parse("2022-11-09")), id = 2),
                    TransactionImportInfo(period = LocalDatePeriod(startDate = LocalDate.parse("2023-01-03"), endDate = LocalDate.parse("2023-03-01")), id = 3),
                    TransactionImportInfo(period = LocalDatePeriod(startDate = LocalDate.parse("2023-03-02"), endDate = LocalDate.parse("2023-04-07")), id = 1),
                ),
                false,
            ),
            tuple(
                LocalDatePeriod(startDate = LocalDate.parse("2023-03-22"), endDate = LocalDate.parse("2023-04-04")),
                listOf(
                    TransactionImportInfo(period = LocalDatePeriod(startDate = LocalDate.parse("2022-07-25"), endDate = LocalDate.parse("2022-09-22")), id = 1),
                    TransactionImportInfo(period = LocalDatePeriod(startDate = LocalDate.parse("2022-09-12"), endDate = LocalDate.parse("2022-11-09")), id = 2),
                    TransactionImportInfo(period = LocalDatePeriod(startDate = LocalDate.parse("2023-01-03"), endDate = LocalDate.parse("2023-03-01")), id = 3),
                    TransactionImportInfo(period = LocalDatePeriod(startDate = LocalDate.parse("2023-03-02"), endDate = LocalDate.parse("2023-04-07")), id = 4),
                ),
                true,
            ),
        ).map { (givenDatePeriod, givenTransactionImportInfos, expectedResult) ->
            DynamicTest.dynamicTest("Given datePeriod:$givenDatePeriod transactionImportInfos:${givenTransactionImportInfos.toString().take(60)}... __Then__ result:$expectedResult") {
                // # When
                val result = MiscUtil.isPeriodFullyImported(givenDatePeriod, givenTransactionImportInfos)
                // # Then
                assertEquals(expectedResult, result)
            }
        }
    }
}