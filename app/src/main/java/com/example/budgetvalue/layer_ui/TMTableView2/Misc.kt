package com.example.budgetvalue.layer_ui.TMTableView2

import android.view.View
import com.tminus1010.tmcommonkotlin_rx.toBehaviorSubject
import io.reactivex.rxjava3.subjects.BehaviorSubject

val scrollObservable = BehaviorSubject.create<Pair<View, Int>>() // TODO("Hacky")
var ignoreScroll = false // TODO("Hacky")
val scrollPosObservable = scrollObservable
    .map { it.second }
    .startWithItem(0)
    .scan(0) { acc, value -> acc + value }
    .toBehaviorSubject()