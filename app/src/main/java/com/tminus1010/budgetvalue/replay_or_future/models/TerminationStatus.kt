package com.tminus1010.budgetvalue.replay_or_future.models

import java.time.LocalDate

sealed class TerminationStatus(val displayStr: String, val ordinal: Long) {
    object PERMANENT : TerminationStatus("Permanent", 0)
    object WAITING_FOR_MATCH : TerminationStatus("Active", 1)

    // TODO("Rename")
    class TERMINATED(val terminationDate: LocalDate) : TerminationStatus("Terminated", ordinal) {
        companion object {
            const val ordinal = 2L
        }
    }
}