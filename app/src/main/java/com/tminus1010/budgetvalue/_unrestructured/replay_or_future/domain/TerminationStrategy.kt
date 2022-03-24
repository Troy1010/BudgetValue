package com.tminus1010.budgetvalue._unrestructured.replay_or_future.domain

import java.time.LocalDate

sealed class TerminationStrategy(val displayStr: String, val ordinal: Long) {
    object PERMANENT : TerminationStrategy("Permanent", 0)
    object WAITING_FOR_MATCH : TerminationStrategy("Active", 1)

    // TODO("Rename")
    class TERMINATED(val terminationDate: LocalDate) : TerminationStrategy("Terminated", ordinal) {
        companion object {
            const val ordinal = 2L
        }
    }
}