package com.tminus1010.buva.domain

import com.tminus1010.buva.core_testing.shared.Given
import com.tminus1010.tmcommonkotlin.tuple.tuple
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import java.math.BigDecimal
import java.time.LocalDate

internal class GuessPastUtilTest {
    @TestFactory
    fun guessAccountsTotalInPast(): List<DynamicTest> {
        return listOf(
            tuple(
                "No Transactions",
                LocalDate.parse("2023-01-01"),
                AccountsAggregate(listOf(Account("Account 1", BigDecimal("2000")))),
                listOf(),
                listOf(),
                BigDecimal("2000"),
            ),
            tuple(
                "1 Transaction",
                LocalDate.parse("2023-01-01"),
                AccountsAggregate(listOf(Account("Account 1", BigDecimal("2000")))),
                listOf(
                    TransactionBlock(
                        "2023-01-10".toLocalDate().toPeriod(7),
                        listOf(
                            Transaction(
                                LocalDate.parse("2023-01-12"),
                                description = "fakeDescription",
                                amount = BigDecimal("-60.00"),
                                categoryAmounts = CategoryAmounts(),
                                categorizationDate = null,
                                "fakeID"
                            )
                        ),
                        true,
                    )
                ),
                listOf<Reconciliation>(),
                Money("2060"),
            ),
        ).map { (testName, date, accountsAggregate, transactionBlocks, reconciliations, expected) ->
            DynamicTest.dynamicTest(testName) {
                // # When
                val result = GuessPastUtil.guessAccountsTotalInPast(date, accountsAggregate, transactionBlocks, reconciliations)
                // # Then
                assertEquals(expected, result)
            }
        }
    }

    @TestFactory
    fun budgettedAmountInPast(): List<DynamicTest> {
        return listOf(
            tuple(
                "No Transactions",
                Given.categoryS,
                LocalDate.parse("2023-01-01"),
                CategoryAmounts(),
                listOf<TransactionBlock>(),
                listOf<Reconciliation>(),
                BigDecimal("0"),
            ),
            tuple(
                "1 Transaction",
                Given.categoryS,
                LocalDate.parse("2023-01-01"),
                mapOf(
                    Given.categoryS to Money("2500"),
                ).toCategoryAmounts(),
                listOf(
                    TransactionBlock(
                        LocalDatePeriod(LocalDate.parse("2023-01-10"), 7),
                        listOf(
                            Transaction(
                                date = LocalDate.parse("2023-01-12"),
                                description = "fakeDescription",
                                amount = BigDecimal("-60.00"),
                                categoryAmounts = CategoryAmounts(),
                                categorizationDate = null,
                                "fakeID"
                            ).categorize(Given.categoryS)
                        ),
                        true,
                    )
                ),
                listOf(),
                Money("2560"),
            ),
        ).map { (testName, category, date, budgetedCAs, transactionBlocks, reconciliations, expected) ->
            DynamicTest.dynamicTest(testName) {
                // # When
                val result = GuessPastUtil.budgettedAmountInPast(category, date, budgetedCAs, transactionBlocks, reconciliations)
                // # Then
                assertEquals(expected, result)
            }
        }
    }
}