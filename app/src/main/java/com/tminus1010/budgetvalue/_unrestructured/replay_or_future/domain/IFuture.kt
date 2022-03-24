package com.tminus1010.budgetvalue._unrestructured.replay_or_future.domain

interface IFuture : IReplayOrFuture {
    val terminationStrategy: TerminationStrategy
    val isAutomatic: Boolean
}