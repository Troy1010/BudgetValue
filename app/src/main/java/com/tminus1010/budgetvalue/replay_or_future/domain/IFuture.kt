package com.tminus1010.budgetvalue.replay_or_future.domain

interface IFuture : IReplayOrFuture {
    val terminationStrategy: TerminationStrategy
    val isAutomatic: Boolean
}