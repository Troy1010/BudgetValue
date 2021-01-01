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
    private val changePublisher = PublishSubject.create<Change<K, V>>()
    private val _itemObservableMap = mutableMapOf<K, BehaviorSubject<V>>()
    init { putAll(map) }
    // Currently, there are two patterns available here (I am deciding which one is better):
    //  > subscribe to itemObservableMap for edits, and additionOrRemovals for additions/removals
    //  > subscribe to changeSet for everything, but you'll need to filter for exactly what you need.
    /**
     * this observable emits a Change every time an entry is added, removed, or edited.
     */
    val changeSet: Observable<Change<K, V>> = changePublisher
    /**
     * this observable emits an AdditionOrRemoval every time an entry is added or removed.
     */
    val additionOrRemovals: Observable<AdditionOrRemoval<K, V>> = changeSet
        .filter { it.changeType == ChangeType.ADD || it.changeType == ChangeType.REMOVE }
        .map {
            val additionOrRemovalType = when (it.changeType) {
                ChangeType.ADD -> AdditionOrRemovalType.ADD
                ChangeType.REMOVE -> AdditionOrRemovalType.REMOVE
                else -> error("Unexpected ChangeType:$it")
            }
            AdditionOrRemoval(additionOrRemovalType, it.key, it.value)
        }
    /**
     * this observable emits whenever SourceHashMap is edited.
     * It exposes item observables.
     */
    val itemObservableMap: BehaviorSubject<Map<K, BehaviorSubject<V>>> = observableMapPublisher
        .startWithItem(_itemObservableMap)
        .map { _itemObservableMap.toMap() }
        .toBehaviorSubject()
    
    fun getEdits(key: K) =
        changeSet
            .filter { it.key == key }
            .takeUntil { it.changeType == ChangeType.REMOVE }
            .filter { it.changeType == ChangeType.EDIT }

    private fun createItemObservable(key: K, value: V): BehaviorSubject<V> {
        return BehaviorSubject.createDefault(value)
            .also { it.skip(1).subscribe { super.put(key, it) } } // TODO("dispose")
    }

    // # Override HashMap functions

    override fun putAll(from: Map<out K, V>) {
        super.putAll(from)
        from.forEach { (key, value) ->
            _itemObservableMap[key]?.also { subject ->
                changePublisher.onNext(Change(ChangeType.EDIT, key, value))
                subject.onNext(value)
            } ?: run {
                changePublisher.onNext(Change(ChangeType.ADD, key, value))
                _itemObservableMap[key] = createItemObservable(key, value)
            }
        }
        observableMapPublisher.onNext(_itemObservableMap)
    }

    override fun put(key: K, value: V): V? {
        val x = super.put(key, value)
        _itemObservableMap[key]?.also { subject ->
            changePublisher.onNext(Change(ChangeType.EDIT, key, value))
            subject.onNext(value)
        } ?: run {
            changePublisher.onNext(Change(ChangeType.ADD, key, value))
            _itemObservableMap[key] = createItemObservable(key, value)
        }
        observableMapPublisher.onNext(_itemObservableMap)
        return x
    }

    override fun clear() {
        super.clear()
        _itemObservableMap.forEach { (key, subject) ->
            exitValueBox?.also { (exitValue) ->
                changePublisher.onNext(Change(ChangeType.EDIT, key, exitValue))
                subject.onNext(exitValue)
            }
            changePublisher.onNext(Change(ChangeType.REMOVE, key, subject.value))
        }
        _itemObservableMap.clear()
        observableMapPublisher.onNext(_itemObservableMap)
    }

    override fun remove(key: K): V? {
        val x = super.remove(key)
        _itemObservableMap[key]?.also { subject ->
            exitValueBox?.also { (exitValue) ->
                changePublisher.onNext(Change(ChangeType.EDIT, key, exitValue))
                subject.onNext(exitValue)
            }
            changePublisher.onNext(Change(ChangeType.REMOVE, key, subject.value))
            _itemObservableMap.remove(key)
            observableMapPublisher.onNext(_itemObservableMap)
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
