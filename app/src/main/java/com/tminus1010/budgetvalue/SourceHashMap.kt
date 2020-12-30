package com.tminus1010.budgetvalue

import com.tminus1010.tmcommonkotlin.tuple.Box
import com.tminus1010.tmcommonkotlin_rx.toBehaviorSubject
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.function.BiFunction
import java.util.function.Function

class SourceHashMap<K, V> private constructor(map: Map<K, V> = emptyMap()): HashMap<K, V>() {
    var exitValueBox: Box<V>? = null
    constructor(map: Map<K, V> = emptyMap(), exitValue: V): this(map) { exitValueBox = Box(exitValue) }
    private val observableMapPublisher = PublishSubject.create<MutableMap<K, BehaviorSubject<V>>>()
    val additionsObservablePublisher = PublishSubject.create<Map.Entry<K, BehaviorSubject<V>>>()
    val observableMap = mutableMapOf<K, BehaviorSubject<V>>()
    init { putAll(map) }
    /**
     * this observable emits whenever SourceHashMap is added to.
     * It emits a K : BehaviorSubject<V> pair
     */
    val additions = additionsObservablePublisher
        .startWithIterable(observableMap.entries)
    /**
     * this observable emits whenever SourceHashMap is edited.
     * It emits a map of K : BehaviorSubject<V>
     */
    val observable = observableMapPublisher
        .startWithItem(observableMap)
        .map { observableMap.toMap() }
        .toBehaviorSubject()

    private fun createItemObservable(key: K, value: V): BehaviorSubject<V> {
        return BehaviorSubject.createDefault(value)
            .also { it.skip(1).subscribe { super.put(key, it) } } // TODO("dispose")
    }

    // # Override HashMap functions

    override fun clear() {
        super.clear()
        exitValueBox?.also { v -> observableMap.forEach { it.value.onNext(v.unbox()) } }
        observableMap.clear()
        observableMapPublisher.onNext(observableMap)
    }

    override fun putAll(from: Map<out K, V>) {
        super.putAll(from)
        for ((key, value) in from) {
            if (key in observableMap.keys) {
                observableMap[key]!!.onNext(value)
            } else {
                createItemObservable(key, value)
                    .also { observableMap.put(key, it) }
                    .also { additionsObservablePublisher.onNext(createMapEntry(key, it)) }
            }
        }
        observableMapPublisher.onNext(observableMap)
    }

    override fun put(key: K, value: V): V? {
        val x = super.put(key, value)
        if (key in observableMap.keys) {
            observableMap[key]!!.onNext(value)
        } else {
            createItemObservable(key, value)
                .also { observableMap.put(key, it) }
                .also { additionsObservablePublisher.onNext(createMapEntry(key, it)) }
        }
        observableMapPublisher.onNext(observableMap)
        return x
    }

    override fun remove(key: K): V? {
        val x = super.remove(key)
        exitValueBox?.also { v -> observableMap[key]!!.onNext(v.unbox()) }
        observableMap.remove(key)
        observableMapPublisher.onNext(observableMap)
        return x
    }

    override fun remove(key: K, value: V): Boolean {
        TODO()
    }

    override fun putIfAbsent(key: K, value: V): V? {
        TODO()
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
