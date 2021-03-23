package com.tminus1010.budgetvalue.middleware.ui.TMTableView2

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.tminus1010.budgetvalue.middleware.Orientation
import com.tminus1010.tmcommonkotlin.rx.extensions.toBehaviorSubject
import io.reactivex.rxjava3.subjects.BehaviorSubject

class SynchronizedScrollListener(
    val orientation: Orientation = Orientation.HORIZONTAL,
) : RecyclerView.OnScrollListener() {
    /**
     * Here are my possible solution groups for the requirement: Scroll without listening
     *       1) Ignore ALL while doing synchronized scrolling.
     *          ! Might fail under new scrolling features
     *       2) Keep track of synchronized scrolls and ignore exact matches.
     *          ! Not fast
     *          ! More complicated; prone to human error
     *
     * Currently, I am using the first option
     */
    var ignoreScroll = false
    val scrollObservable = BehaviorSubject.create<Pair<View, Int>>()
    val scrollPosObservable = scrollObservable
        .map { it.second }
        .startWithItem(0)
        .scan(0) { acc, value -> acc + value }
        .toBehaviorSubject()

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        val dv = if (orientation == Orientation.HORIZONTAL) dx else dy
        if (!ignoreScroll) scrollObservable.onNext(Pair(recyclerView, dv))
        super.onScrolled(recyclerView, dx, dy)
    }

    fun ignoredScrollBy(rv: RecyclerView, dx: Int, dy: Int) {
        ignoreScroll = true
        rv.scrollBy(dx, dy)
        ignoreScroll = false
    }
}