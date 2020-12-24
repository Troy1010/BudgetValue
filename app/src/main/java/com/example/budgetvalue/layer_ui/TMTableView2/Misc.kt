package com.example.budgetvalue.layer_ui.TMTableView2

import android.view.View
import com.example.budgetvalue.layer_ui.TMTableView.IViewItemRecipe
import com.tminus1010.tmcommonkotlin_rx.toBehaviorSubject
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlin.math.max

val recipe2D = BehaviorSubject.create<Iterable<Iterable<IViewItemRecipe>>>()  // TODO("Hacky, only supports 1")
val firstColWidth = recipe2D
    .map {
        it
            .map { it.first().intrinsicWidth }
            .fold(0) { acc, v -> max(acc, v) }
    }
    .toBehaviorSubject()
val scrollObservable = BehaviorSubject.create<Pair<View, Int>>() // TODO("Hacky")
var ignoreScroll = false // TODO("Hacky")
val scrollPosObservable = scrollObservable
    .map { it.second }
    .startWithItem(0)
    .scan(0) { acc, value -> acc + value }
    .toBehaviorSubject()