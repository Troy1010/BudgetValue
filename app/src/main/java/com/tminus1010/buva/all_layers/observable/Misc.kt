package com.tminus1010.buva.all_layers.observable

import com.tminus1010.tmcommonkotlin.rx3.extensions.boxStartNull
import com.tminus1010.tmcommonkotlin.rx3.extensions.isCold
import com.tminus1010.tmcommonkotlin.tuple.Quadruple
import com.tminus1010.tmcommonkotlin.tuple.Quintuple
import com.tminus1010.tmcommonkotlin.tuple.Septuple
import com.tminus1010.tmcommonkotlin.tuple.Sextuple
import io.reactivex.rxjava3.core.Observable

data class IndexAndTuple<T>(
    val index: Int,
    val tuple: T,
)

fun <A, B> combineLatestWithIndex(
    a: Observable<A>,
    b: Observable<B>,
): Observable<Triple<Int, A, B>> {
    return Observable.merge(a.map { 0 }, b.map { 1 })
        .zipWith(Rx.combineLatest(a, b)) { index, tuple -> IndexAndTuple(index, tuple) }
        .map { Triple(it.index, it.tuple.first, it.tuple.second) }
}

fun <A, B, C> combineLatestWithIndex(
    a: Observable<A>,
    b: Observable<B>,
    c: Observable<C>,
): Observable<Quadruple<Int, A, B, C>> {
    return Observable.zip(
        Observable.merge(a.map { 0 }, b.map { 1 }, c.map { 2 }),
        Rx.combineLatest(a, b, c)
    ) { index, tuple -> IndexAndTuple(index, tuple) }
        .map { Quadruple(it.index, it.tuple.first, it.tuple.second, it.tuple.third) }
}

@Suppress("UNCHECKED_CAST")
fun <A, B> mergeCombineWithIndex(
    a: Observable<A>,
    b: Observable<B>,
): Observable<Triple<Int, A?, B?>> {
    return Observable.zip(
        Observable.merge(a.map { 0 }, b.map { 1 }),
        Observable.merge(a, b)
    ) { i, v -> Pair(i, v) }
        .scan(Triple<Int, A?, B?>(-1, null, null)) { acc, (i, v) ->
            when (i) {
                0 -> acc.copy(first = 0, second = v as A?)
                1 -> acc.copy(first = 1, third = v as B?)
                else -> error("Unhandled i:$i")
            }
        }
        .skip(1)
}

@Suppress("UNCHECKED_CAST")
fun <A, B, C> mergeCombineWithIndex(
    a: Observable<A>,
    b: Observable<B>,
    c: Observable<C>,
): Observable<Quadruple<Int, A?, B?, C?>> {
    return Observable.zip(
        Observable.merge(a.map { 0 }, b.map { 1 }, c.map { 2 }),
        Observable.merge(a, b, c)
    ) { i, v -> Pair(i, v) }
        .scan(Quadruple<Int, A?, B?, C?>(-1, null, null, null)) { acc, (i, v) ->
            when (i) {
                0 -> acc.copy(first = 0, second = v as A?)
                1 -> acc.copy(first = 1, third = v as B?)
                2 -> acc.copy(first = 2, fourth = v as C?)
                else -> error("Unhandled i:$i")
            }
        }
        .skip(1)
}

@Suppress("UNCHECKED_CAST")
fun <A, B, C, D> mergeCombineWithIndex(
    a: Observable<A>,
    b: Observable<B>,
    c: Observable<C>,
    d: Observable<D>,
): Observable<Quintuple<Int, A?, B?, C?, D?>> {
    return Observable.zip(
        Observable.merge(a.map { 0 }, b.map { 1 }, c.map { 2 }, d.map { 3 }),
        Observable.merge(a, b, c, d)
    ) { i, v -> Pair(i, v) }
        .scan(Quintuple<Int, A?, B?, C?, D?>(-1, null, null, null, null)) { acc, (i, v) ->
            when (i) {
                0 -> acc.copy(first = 0, second = v as A?)
                1 -> acc.copy(first = 1, third = v as B?)
                2 -> acc.copy(first = 2, fourth = v as C?)
                3 -> acc.copy(first = 3, fifth = v as D?)
                else -> error("Unhandled i:$i")
            }
        }
        .skip(1)
}

fun <A : Any, B : Any> combineLatestImpatient(
    a: Observable<A>,
    b: Observable<B>,
): Observable<Pair<A?, B?>> {
    return Rx.combineLatest(a.boxStartNull(), b.boxStartNull())
        .compose { observable ->
            // # If no observables are cold, then skip the first emission
            // * The observables start with null so that combineLatest is impatient.
            //   However, we cannot assume that the first emission will be that initial skippable
            //   tuple of nulls, because cold observables will emit their latest value even at the
            //   first emission.
            if (listOf(a, b).none { it.isCold() }) {
                observable.skip(1)
            } else observable
        }
        .map { Pair(it.first.first, it.second.first) }
}

fun <A : Any, B : Any, C : Any> combineLatestImpatient(
    a: Observable<A>,
    b: Observable<B>,
    c: Observable<C>,
): Observable<Triple<A?, B?, C?>> {
    return Rx.combineLatest(a.boxStartNull(), b.boxStartNull(), c.boxStartNull())
        .compose { observable ->
            // # If no observables are cold, then skip the first emission
            // * The observables start with null so that combineLatest is impatient.
            //   However, we cannot assume that the first emission will be that initial skippable
            //   tuple of nulls, because cold observables will emit their latest value even at the
            //   first emission.
            if (listOf(a, b, c).none { it.isCold() }) {
                observable.skip(1)
            } else observable
        }
        .map { Triple(it.first.first, it.second.first, it.third.first) }
}

fun <A : Any, B : Any, C : Any, D : Any> combineLatestImpatient(
    a: Observable<A>,
    b: Observable<B>,
    c: Observable<C>,
    d: Observable<D>,
): Observable<Quadruple<A?, B?, C?, D?>> {
    return Rx.combineLatest(
        a.boxStartNull(),
        b.boxStartNull(),
        c.boxStartNull(),
        d.boxStartNull()
    )
        .compose { observable ->
            // # If no observables are cold, then skip the first emission
            // * The observables start with null so that combineLatest is impatient.
            //   However, we cannot assume that the first emission will be that initial skippable
            //   tuple of nulls, because cold observables will emit their latest value even at the
            //   first emission.
            if (listOf(a, b, c, d).none { it.isCold() }) {
                observable.skip(1)
            } else observable
        }
        .map { Quadruple(it.first.first, it.second.first, it.third.first, it.fourth.first) }
}

fun <A : Any, B : Any, C : Any, D : Any, E : Any> combineLatestImpatient(
    a: Observable<A>,
    b: Observable<B>,
    c: Observable<C>,
    d: Observable<D>,
    e: Observable<E>,
): Observable<Quintuple<A?, B?, C?, D?, E?>> {
    return Rx.combineLatest(
        a.boxStartNull(),
        b.boxStartNull(),
        c.boxStartNull(),
        d.boxStartNull(),
        e.boxStartNull()
    )
        .compose { observable ->
            // # If no observables are cold, then skip the first emission
            // * The observables start with null so that combineLatest is impatient.
            //   However, we cannot assume that the first emission will be that initial skippable
            //   tuple of nulls, because cold observables will emit their latest value even at the
            //   first emission.
            if (listOf(a, b, c, d, e).none { it.isCold() }) {
                observable.skip(1)
            } else observable
        }
        .map { Quintuple(it.first.first, it.second.first, it.third.first, it.fourth.first, it.fifth.first) }
}

fun <A : Any, B : Any, C : Any, D : Any, E : Any, F : Any> combineLatestImpatient(
    a: Observable<A>,
    b: Observable<B>,
    c: Observable<C>,
    d: Observable<D>,
    e: Observable<E>,
    f: Observable<F>,
): Observable<Sextuple<A?, B?, C?, D?, E?, F?>> {
    return Rx.combineLatest(
        a.boxStartNull(),
        b.boxStartNull(),
        c.boxStartNull(),
        d.boxStartNull(),
        e.boxStartNull(),
        f.boxStartNull()
    )
        .compose { observable ->
            // # If no observables are cold, then skip the first emission
            // * The observables start with null so that combineLatest is impatient.
            //   However, we cannot assume that the first emission will be that initial skippable
            //   tuple of nulls, because cold observables will emit their latest value even at the
            //   first emission.
            if (listOf(a, b, c, d, e, f).none { it.isCold() }) {
                observable.skip(1)
            } else observable
        }
        .map { Sextuple(it.first.first, it.second.first, it.third.first, it.fourth.first, it.fifth.first, it.sixth.first) }
}

fun <A : Any, B : Any, C : Any, D : Any, E : Any, F : Any, G : Any> combineLatestImpatient(
    a: Observable<A>,
    b: Observable<B>,
    c: Observable<C>,
    d: Observable<D>,
    e: Observable<E>,
    f: Observable<F>,
    g: Observable<G>,
): Observable<Septuple<A?, B?, C?, D?, E?, F?, G?>> {
    return Rx.combineLatest(
        a.boxStartNull(),
        b.boxStartNull(),
        c.boxStartNull(),
        d.boxStartNull(),
        e.boxStartNull(),
        f.boxStartNull(),
        g.boxStartNull()
    )
        .compose { observable ->
            // # If no observables are cold, then skip the first emission
            // * The observables start with null so that combineLatest is impatient.
            //   However, we cannot assume that the first emission will be that initial skippable
            //   tuple of nulls, because cold observables will emit their latest value even at the
            //   first emission.
            if (listOf(a, b, c, d, e, f, g).none { it.isCold() }) {
                observable.skip(1)
            } else observable
        }
        .map { Septuple(it.first.first, it.second.first, it.third.first, it.fourth.first, it.fifth.first, it.sixth.first, it.seventh.first) }
}

// untested
fun <K, V> createMapEntry(key: K, value: V): Map.Entry<K, V> {
    return object : Map.Entry<K, V> {
        override val key: K
            get() = key
        override val value: V
            get() = value
    }
}
