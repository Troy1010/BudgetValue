package com.tminus1010.budgetvalue.replay_or_future.models

import java.time.LocalDate

sealed class TerminationStatus(val displayStr: String) {
    object PERMANENT : TerminationStatus("Permanent")
    object WAITING_FOR_MATCH : TerminationStatus("Active")

    // TODO("Rename")
    class TERMINATED(private val terminationDate: LocalDate) : TerminationStatus("Terminated")
}