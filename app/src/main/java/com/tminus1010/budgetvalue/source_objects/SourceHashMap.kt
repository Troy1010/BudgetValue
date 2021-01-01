package com.tminus1010.budgetvalue.source_objects

import com.tminus1010.tmcommonkotlin.tuple.Box
import com.tminus1010.tmcommonkotlin_rx.toBehaviorSubject
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.function.BiFunction
import java.util.function.Function

class SourceHashMap<K, V> constructor(map: Map<K, V> = emptyMap()): HashMap<K, V>() {
    var exitValueBox: Box<V>? = null
    constructor(map: Map<K, V> = emptyMap(), exitValue: V): this(map) { exitValueBox = Box(exitValue) }
    private val observableMapPublisher = PublishSubject.create<MutableMap<K, BehaviorSubject<V>>>()
    val changePublisher = PublishSubject.create<Change<K, V>>()
    val observableMap = mutableMapOf<K, BehaviorSubject<V>>()
    init { putAll(map) }
    /**
     * this observable emits a Change every time the map is added to, removed from, or edited.
     */
    val changeSet: Observable<Change<K, V>> = changePublisher
    /**
     * this observable emits whenever SourceHashMap is edited.
     */
    val observable: BehaviorSubject<Map<K, BehaviorSubject<V>>> = observableMapPublisher
        .startWithItem(observableMap)
        .map { observableMap.toMap() }
        .toBehaviorSubject()

    private fun createItemObservable(key: K, value: V): BehaviorSubject<V> {
        return BehaviorSubject.createDefault(value)
            .also { it.skip(1).subscribe { super.put(key, it) } } // TODO("dispose")
    }

    // # Override HashMap functions

    override fun putAll(from: Map<out K, V>) {
        super.putAll(from)
        from.forEach { (key, value) ->
            observableMap[key]?.also { subject ->
                changePublisher.onNext(Change(ChangeType.EDIT, key, value))
                subject.onNext(value)
            } ?: run {
                changePublisher.onNext(Change(ChangeType.ADD, key, value))
                observableMap[key] = createItemObservable(key, value)
            }
        }
        observableMapPublisher.onNext(observableMap)
    }

    override fun put(key: K, value: V): V? {
        val x = super.put(key, value)
        observableMap[key]?.also { subject ->
            changePublisher.onNext(Change(ChangeType.EDIT, key, value))
            subject.onNext(value)
        } ?: run {
            changePublisher.onNext(Change(ChangeType.ADD, key, value))
            observableMap[key] = createItemObservable(key, value)
        }
        observableMapPublisher.onNext(observableMap)
        return x
    }

    override fun clear() {
        super.clear()
        observableMap.forEach { (key, subject) ->
            exitValueBox?.also { (exitValue) ->
                changePublisher.onNext(Change(ChangeType.EDIT, key, exitValue))
                subject.onNext(exitValue)
            }
            changePublisher.onNext(Change(ChangeType.REMOVE, key, subject.value))
        }
        observableMap.clear()
        observableMapPublisher.onNext(observableMap)
    }

    override fun remove(key: K): V? {
        val x = super.remove(key)
        observableMap[key]?.also { subject ->
            exitValueBox?.also { (exitValue) ->
                changePublisher.onNext(Change(ChangeType.EDIT, key, exitValue))
                subject.onNext(exitValue)
            }
            changePublisher.onNext(Change(ChangeType.REMOVE, key, subject.value))
            observableMap.remove(key)
            observableMapPublisher.onNext(observableMap)
        }
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
