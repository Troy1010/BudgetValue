package com.example.budgetvalue

import com.tminus1010.tmcommonkotlin_rx.toBehaviorSubject
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.function.BiFunction
import java.util.function.Function

class SourceHashMap<K, V>(): HashMap<K, V>() {
    constructor(map: Map<K, V>): this() { putAll(map) }
    private val observableMapPublisher = PublishSubject.create<MutableMap<K, BehaviorSubject<V>>>()
    val additionsObservablePublisher = PublishSubject.create<Map.Entry<K, BehaviorSubject<V>>>()
    val observableMap = mutableMapOf<K, BehaviorSubject<V>>()
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

    // this logic could be improved in the next iteration
    fun prepareKey(key: K) {
        val value = BehaviorSubject.create<V>()
            .also { it.skip(1).subscribe { super.put(key, it) } } // TODO("dispose")
        observableMap.put(key, value)
        additionsObservablePublisher.onNext(object: Map.Entry<K, BehaviorSubject<V>> {
            override val key: K
                get() = key
            override val value: BehaviorSubject<V>
                get() = value
        })
        observableMapPublisher.onNext(observableMap)
    }

    // # Override HashMap functions

    override fun clear() {
        super.clear()
        observableMap.clear()
        observableMapPublisher.onNext(observableMap)
    }

    override fun putAll(from: Map<out K, V>) {
        super.putAll(from)
        val fromRedefined = from.mapValues { createItemObservable(it.key, it.value) }
        observableMap.putAll(fromRedefined)
        fromRedefined.entries.forEach { additionsObservablePublisher.onNext(it) }
        observableMapPublisher.onNext(observableMap)
    }

    // TODO("might just need to onNext, not create a new observable every time")
    override fun put(key: K, value: V): V? {
        val x = super.put(key, value)
        val valueRedefined = createItemObservable(key, value)
        observableMap.put(key, valueRedefined)
        additionsObservablePublisher.onNext(object: Map.Entry<K, BehaviorSubject<V>> {
            override val key: K
                get() = key
            override val value: BehaviorSubject<V>
                get() = valueRedefined
        })
        observableMapPublisher.onNext(observableMap)
        return x
    }

    override fun putIfAbsent(key: K, value: V): V? {
        val x = super.putIfAbsent(key, value)
        val valueRedefined = createItemObservable(key, value)
        observableMap.putIfAbsent(key, valueRedefined)
        if (x!=null) {
            additionsObservablePublisher.onNext(object: Map.Entry<K, BehaviorSubject<V>> {
                override val key: K
                    get() = key
                override val value: BehaviorSubject<V>
                    get() = valueRedefined
            })
            observableMapPublisher.onNext(observableMap)
        }
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
