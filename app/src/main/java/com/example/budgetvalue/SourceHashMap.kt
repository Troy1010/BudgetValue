package com.example.budgetvalue

import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import com.example.budgetvalue.extensions.startWith
import com.tminus1010.tmcommonkotlin_rx.toBehaviorSubject

class SourceHashMap<K, V> : HashMap<K, V>() {
    private val publisher = PublishSubject.create<Unit>()
    private val observableMap = mutableMapOf<K, BehaviorSubject<V>>()
    /**
     * this observable emits whenever SourceHashMap puts or removes.
     * It emits a map of K : BehaviorSubject<V>
     */
    val observable = publisher
        .startWith(Unit)
        .map { observableMap }
        .toBehaviorSubject()

    fun createItemObservable(key: K, value: V): BehaviorSubject<V> {
        return BehaviorSubject.createDefault(value)
            .also { it.skip(1).subscribe { super.put(key, it) } } // TODO("dispose")
    }

    override fun clear() {
        super.clear()
        observableMap.clear()
        publisher.onNext(Unit)
    }

    override fun putAll(from: Map<out K, V>) {
        super.putAll(from)
        observableMap.putAll(from.mapValues { createItemObservable(it.key, it.value) })
        publisher.onNext(Unit)
    }

    override fun put(key: K, value: V): V? {
        val x = super.put(key, value)
        observableMap.put(key, createItemObservable(key, value))
        publisher.onNext(Unit)
        return x
    }

    override fun remove(key: K): V? {
        val x = super.remove(key)
        observableMap.remove(key)
        publisher.onNext(Unit)
        return x
    }
}
