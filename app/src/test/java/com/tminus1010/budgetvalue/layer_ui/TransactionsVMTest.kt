package com.tminus1010.budgetvalue.layer_ui

//class TransactionsVMTest {
//    val repo = mockk<Repo>()
//        .also { every { it.transactions } returns Observable.just(listOf()) }
//        .also { every { it.fetchAnchorDateOffset() } returns Observable.just(0) }
//        .also { every { it.fetchBlockSize() } returns Observable.just(14) }
//    val datePeriodGetter = DatePeriodGetter(repo)
//    val transactionsVM = TransactionsVM(repo, datePeriodGetter)
//
//    @Test
//    fun getBlocksFromTransactions() {
//        // # Given
//        val transactions = listOf(
//            Transaction(
//                LocalDate.of(2020, Month.JULY, 22),
//                "",
//                14.52.toBigDecimal(),
//                hashMapOf(),
//                1
//            ),
//            Transaction(
//                LocalDate.of(2020, Month.JULY, 1),
//                "",
//                5.toBigDecimal(),
//                hashMapOf(),
//                2
//            ),
//            Transaction(
//                LocalDate.of(2020, Month.JULY, 1),
//                "",
//                10.toBigDecimal(),
//                hashMapOf(),
//                3
//            ),
//            Transaction(
//                LocalDate.of(2020, Month.AUGUST, 22),
//                "",
//                111.11.toBigDecimal(),
//                hashMapOf(),
//                4
//            ),
//            Transaction(
//                LocalDate.of(2020, Month.AUGUST, 3),
//                "",
//                222.toBigDecimal(),
//                hashMapOf(),
//                5
//            ),
//        )
//        // # Stimulate
//        val result = transactionsVM.getBlocksFromTransactions(transactions)
//        // # Verify
//        assertEquals(
//            listOf(
//                Block(LocalDatePeriod(LocalDate.parse("2020-07-01"), LocalDate.parse("2020-07-14")),
//                    15.toBigDecimal(),
//                    hashMapOf()),
//                Block(LocalDatePeriod(LocalDate.parse("2020-07-15"), LocalDate.parse("2020-07-28")),
//                    14.52.toBigDecimal(),
//                    hashMapOf()),
//                Block(LocalDatePeriod(LocalDate.parse("2020-07-29"), LocalDate.parse("2020-08-11")),
//                    222.toBigDecimal(),
//                    hashMapOf()),
//                Block(LocalDatePeriod(LocalDate.parse("2020-08-12"), LocalDate.parse("2020-08-25")),
//                    111.11.toBigDecimal(),
//                    hashMapOf()),
//            ),
//            result
//        )
//    }
//}