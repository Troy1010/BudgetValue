package com.tminus1010.budgetvalue.replay.models

interface IReplay : IReplayOrFuture {
    val isAutoReplay: Boolean
}