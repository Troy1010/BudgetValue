package com.example.budgetvalue.util

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.Observer
import com.example.budgetvalue.models.Category
import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableSource
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.math.BigDecimal

fun <T> ObservableSource<T>.toLiveData2(): LiveData<T> {
    return convertRXToLiveData2(this)
}

fun <T> convertRXToLiveData2 (observable: ObservableSource<T>): LiveData<T> {
    return LiveDataReactiveStreams.fromPublisher(Flowable.fromObservable(observable, BackpressureStrategy.DROP))
}

// This might be buggy..
fun <T> Observable<T>.toBehaviorSubject(): BehaviorSubject<T> {
    val behaviorSubject = BehaviorSubject.create<T>()
    this.subscribe(behaviorSubject)
    return behaviorSubject
}

fun <A, B, C, D, E> combineLatestAsTuple(a: ObservableSource<A>, b: ObservableSource<B>, c: ObservableSource<C>, d: ObservableSource<D>, e: ObservableSource<E>): Observable<Quintuple<A, B, C, D, E>> {
    return Observable.combineLatest(
        listOf(a, b, c, d, e)
    ) {
        Quintuple(
            it[0] as A,
            it[1] as B,
            it[2] as C,
            it[3] as D,
            it[4] as E
        )
    }
}
fun <A, B, C, D> combineLatestAsTuple(a: ObservableSource<A>, b: ObservableSource<B>, c: ObservableSource<C>, d: ObservableSource<D>): Observable<Quadruple<A, B, C, D>> {
    return Observable.combineLatest(
        listOf(a, b, c, d)
    ) {
        Quadruple(
            it[0] as A,
            it[1] as B,
            it[2] as C,
            it[3] as D
        )
    }
}

fun <A, B> combineLatestAsTuple(a: ObservableSource<A>, b: ObservableSource<B>): Observable<Pair<A, B>> {
    return Observable.combineLatest(
        listOf(a, b)
    ) {
        Pair(
            it[0] as A,
            it[1] as B
        )
    }
}



fun <T> LiveData<T>.observeOnce(action: (T?) -> Unit) {
    this.value
    val observer = object : Observer<T> {
        override fun onChanged(o: T?) {
            this@observeOnce.removeObserver(this)
            action(o)
        }
    }
    this.observeForever(observer)
}

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, action:(T?) -> Unit) {
    this.value
    val observer = object : Observer<T> {
        override fun onChanged(o: T?) {
            this@observeOnce.removeObserver(this)
            action(o)
        }
    }
    this.observe(lifecycleOwner, observer)
}

fun <T, R> Iterable<T>.zipWithDefault(other: Iterable<R>, default: R): List<Pair<T, R>> {
    val first = iterator()
    val second = other.iterator()
    val list = ArrayList<Pair<T, R>>()
    while (first.hasNext()) {
        val y = if (second.hasNext()) {
            second.next()
        } else {
            default
        }
        list.add(Pair(first.next(), y))
    }
    return list
}