package com.tminus1010.budgetvalue._core.middleware

import com.tminus1010.tmcommonkotlin.tuple.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableSource

object Rx {
    @Suppress("UNCHECKED_CAST")
    fun <A, B, C, D, E, F, G> combineLatest(
        a: ObservableSource<A>,
        b: ObservableSource<B>,
        c: ObservableSource<C>,
        d: ObservableSource<D>,
        e: ObservableSource<E>,
        f: ObservableSource<F>,
        g: ObservableSource<G>,
    ): Observable<Septuple<A, B, C, D, E, F, G>> {
        return Observable.combineLatest(
            listOf(a, b, c, d, e, f, g)
        ) {
            Septuple(
                it[0] as A,
                it[1] as B,
                it[2] as C,
                it[3] as D,
                it[4] as E,
                it[5] as F,
                it[6] as G,
            )
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <A, B, C, D, E, F> combineLatest(
        a: ObservableSource<A>,
        b: ObservableSource<B>,
        c: ObservableSource<C>,
        d: ObservableSource<D>,
        e: ObservableSource<E>,
        f: ObservableSource<F>
    ): Observable<Sextuple<A, B, C, D, E, F>> {
        return Observable.combineLatest(
            listOf(a, b, c, d, e, f)
        ) {
            Sextuple(
                it[0] as A,
                it[1] as B,
                it[2] as C,
                it[3] as D,
                it[4] as E,
                it[5] as F,
            )
        }
    }


    @Suppress("UNCHECKED_CAST")
    fun <A, B, C, D, E> combineLatest(
        a: ObservableSource<A>,
        b: ObservableSource<B>,
        c: ObservableSource<C>,
        d: ObservableSource<D>,
        e: ObservableSource<E>,
    ): Observable<Quintuple<A, B, C, D, E>> {
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

    @Suppress("UNCHECKED_CAST")
    fun <A, B, C, D> combineLatest(
        a: ObservableSource<A>,
        b: ObservableSource<B>,
        c: ObservableSource<C>,
        d: ObservableSource<D>,
    ): Observable<Quadruple<A, B, C, D>> {
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

    @Suppress("UNCHECKED_CAST")
    fun <A, B, C> combineLatest(
        a: ObservableSource<A>,
        b: ObservableSource<B>,
        c: ObservableSource<C>,
    ): Observable<Triple<A, B, C>> {
        return Observable.combineLatest(
            listOf(a, b, c)
        ) {
            Triple(
                it[0] as A,
                it[1] as B,
                it[2] as C
            )
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <A, B> combineLatest(
        a: ObservableSource<A>,
        b: ObservableSource<B>,
    ): Observable<Pair<A, B>> {
        return Observable.combineLatest(
            listOf(a, b)
        ) {
            Pair(
                it[0] as A,
                it[1] as B
            )
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <A> combineLatest(a: ObservableSource<A>): Observable<Box<A>> {
        return Observable.combineLatest(
            listOf(a)
        ) {
            Box(
                it[0] as A
            )
        }
    }

    fun merge(vararg completables: Completable) = Completable.merge(completables.toList())

    inline fun <reified A, reified B> merge(a: ObservableSource<A>, b: ObservableSource<B>): Observable<Pair<A?, B?>> {
        return Observable.merge(a, b)
            .map { Pair(it as? A, it as? B) }
    }
}