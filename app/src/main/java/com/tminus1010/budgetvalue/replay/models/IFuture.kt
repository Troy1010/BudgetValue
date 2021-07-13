package com.tminus1010.budgetvalue.replay.models

interface IFuture : IReplayOrFuture {
    val shouldDeleteAfterCategorization: Boolean
}