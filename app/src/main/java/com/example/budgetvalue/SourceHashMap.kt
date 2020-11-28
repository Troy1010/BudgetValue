package com.example.budgetvalue

import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.function.BiFunction
import java.util.function.Function

class SourceHashMap<K, V> : HashMap<K, V>() {
    private val observableMapPublisher = PublishSubject.create<MutableMap<K, BehaviorSubject<V>>>()
    private val observableMap = mutableMapOf<K, BehaviorSubject<V>>()
    /**
     * this observable emits whenever SourceHashMap is edited.
     * It emits a map of K : BehaviorSubject<V>
     */
    val observable = observableMapPublisher
        .startWithItem(observableMap)
        .map { observableMap.toImmutableMap() }

    private fun createItemObservable(key: K, value: V): BehaviorSubject<V> {
        return BehaviorSubject.createDefault(value)
            .also { it.skip(1).subscribe { super.put(key, it) } } // TODO("dispose")
    }

    // # Override HashMap functions

    override fun clear() {
        super.clear()
        observableMap.clear()
        observableMapPublisher.onNext(observableMap)
    }

    override fun putAll(from: Map<out K, V>) {
        super.putAll(from)
        observableMap.putAll(from.mapValues { createItemObservable(it.key, it.value) })
        observableMapPublisher.onNext(observableMap)
    }

    override fun put(key: K, value: V): V? {
        val x = super.put(key, value)
        observableMap.put(key, createItemObservable(key, value))
        observableMapPublisher.onNext(observableMap)
        return x
    }

    override fun putIfAbsent(key: K, value: V): V? {
        val x = super.putIfAbsent(key, value)
        observableMap.putIfAbsent(key, createItemObservable(key, value))
        observableMapPublisher.onNext(observableMap)
        return x
    }

    override fun remove(key: K): V? {
        val x = super.remove(key)
        observableMap.remove(key)
        observableMapPublisher.onNext(observableMap)
        return x
    }

    override fun remove(key: K, value: V): Boolean {
        val x = super.remove(key, value)
        if (x) observableMap.remove(key)
        observableMapPublisher.onNext(observableMap)
        return x
    }

    override fun replace(key: K, value: V): V? {
        TODO()
    }

    override fun replaceAll(function: BiFunction<in K, in V, out V>) {
        TODO()
    }

    override fun replace(key: K, oldValue: V, newValue: V): Boolean {
        TODO()
    }

    override fun compute(key: K, remappingFunction: BiFunction<in K, in V?, out V?>): V? {
        TODO()
    }

    override fun computeIfAbsent(key: K, mappingFunction: Function<in K, out V>): V {
        TODO()
    }

    override fun computeIfPresent(key: K, remappingFunction: BiFunction<in K, in V, out V?>): V? {
        TODO()
    }

    override fun merge(key: K, value: V, remappingFunction: BiFunction<in V, in V, out V?>): V? {
        TODO()
    }
}
