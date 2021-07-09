package com.tminus1010.budgetvalue.auto_replay.data

import com.tminus1010.budgetvalue.auto_replay.models.AutoReplay
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

interface IAutoReplayRepo {
    fun fetchAutoReplays(): Observable<List<AutoReplay>>
    fun add(autoReplay: AutoReplay): Completable
}