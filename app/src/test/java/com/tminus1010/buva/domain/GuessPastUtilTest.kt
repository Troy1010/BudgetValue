package com.tminus1010.buva.domain

import com.tminus1010.buva.core_testing.shared.Given
import com.tminus1010.tmcommonkotlin.tuple.tuple
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

internal class GuessPastUtilTest {
    @TestFactory
    fun guessAccountsTotalInPast(): List<DynamicTest> {
        return listOf(
            tuple(
                "No Transactions",
                "2023-1-1".toLocalDate(),
                AccountsAggregate(listOf(Account("Account 1", Money("2000")))),
                listOf(),
                listOf(),
                Money("2000"),
            ),
            tuple(
                "1 Transaction",
                "2023-1-1".toLocalDate(),
                AccountsAggregate(listOf(Account("Account 1", Money("2000")))),
                listOf(
                    TransactionBlock(
                        "2023-1-10".toLocalDate().toPeriod(7),
                        listOf(
                            Transaction(
                                "2023-01-12".toLocalDate(),
                                description = "fakeDescription",
                                amount = Money("-60.00"),
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
                "2023-01-01".toLocalDate(),
                CategoryAmounts(),
                listOf<TransactionBlock>(),
                listOf<Reconciliation>(),
                Money("0"),
            ),
            tuple(
                "1 Transaction",
                Given.categoryS,
                "2023-01-01".toLocalDate(),
                CategoryAmounts(
                    Given.categoryS to Money("2500"),
                ),
                listOf(
                    TransactionBlock(
                        LocalDatePeriod("2023-01-10".toLocalDate(), 7),
                        listOf(
                            Transaction(
                                date = "2023-01-12".toLocalDate(),
                                description = "fakeDescription",
                                amount = Money("-60.00"),
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