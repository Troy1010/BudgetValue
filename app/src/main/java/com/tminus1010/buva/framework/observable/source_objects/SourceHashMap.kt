package com.tminus1010.buva.framework.observable.source_objects

import com.tminus1010.buva.all_layers.extensions.copy
import com.tminus1010.tmcommonkotlin.core.extensions.removeIf
import com.tminus1010.tmcommonkotlin.rx3.extensions.toBehaviorSubject
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.rx3.asFlow
import java.util.function.BiFunction
import java.util.function.Function

class SourceHashMap<K, V : Any> constructor(map: Map<K, V> = emptyMap(), val exitValue: V? = null) : HashMap<K, V>() {
    constructor(exitValue: V?, vararg entries: Pair<K, V>) : this(entries.associate { it.first to it.second }, exitValue)

    private val observableMapPublisher = PublishSubject.create<MutableMap<K, BehaviorSubject<V>>>()
    private val changePublisher = PublishSubject.create<Change<K, V>>()
    private val _itemObservableMap = mutableMapOf<K, BehaviorSubject<V>>()

    init {
        putAll(map)
    }
    // Currently, there are two patterns available here (I am deciding which one is better):
    //  > subscribe to itemObservableMap for edits, and additionOrRemovals for additions/removals
    //  > subscribe to changeSet for everything, but you'll need to filter for exactly what you
    //    need, and pairwise/two-way-binding requires extra logic.
    /**
     * this observable emits a Change every time an entry is added, removed, or edited.
     */
    val changeSet: Observable<Change<K, V>> = changePublisher

    /**
     * this observable emits an AdditionOrRemoval every time an entry is added or removed.
     */
    val additionOrRemovals: Observable<AdditionOrRemoval<K, V>> = changeSet
        .filter { it.type == AddRemEditType.ADD || it.type == AddRemEditType.REMOVE }
        .map {
            val additionOrRemovalType =
                when (it.type) {
                    AddRemEditType.ADD -> AddRemType.ADD
                    AddRemEditType.REMOVE -> AddRemType.REMOVE
                    else -> error("Unexpected ChangeType:$it")
                }
            AdditionOrRemoval(additionOrRemovalType, it.key, it.value)
        }

    /**
     * this observable emits whenever SourceHashMap is changed. (only once per transaction)
     * It exposes item observables.
     */
    val observable: BehaviorSubject<Map<K, V>> = changeSet
        .map { this.toMap() }
        .startWithItem(this)
        .toBehaviorSubject()

    /**
     * this is the flow version of [observable]
     */
    val flow = observable.asFlow()

    /**
     * this observable emits whenever an entry is added or removed. (only once per transaction)
     * It exposes item observables.
     */
    val itemObservableMap: Observable<Map<K, BehaviorSubject<V>>> = additionOrRemovals
        .map { _itemObservableMap }
        .startWithItem(_itemObservableMap)
        .map { it.toMap() }
        .retry(100) // Duct-tape solution to avoid Concurrent Modification errors, but those might not exist anymore
        .toBehaviorSubject()

    /**
     * this is the flow version of [itemObservableMap]
     */
    val itemFlowMap = itemObservableMap
        .map { it.mapValues { runBlocking { it.value.asFlow().stateIn(GlobalScope) } } }
        .asFlow()

    val allEdits =
        changeSet
            .filter { it.type == AddRemEditType.EDIT }
            .publish().refCount()

    fun getEdits(key: K) =
        changeSet
            .filter { it.key == key }
            .takeUntil { it.type == AddRemEditType.REMOVE }
            .filter { it.type == AddRemEditType.EDIT }

    private fun createItemObservable(key: K, value: V): BehaviorSubject<V> {
        return BehaviorSubject.createDefault(value)
            .also { it.skip(1).subscribe { super.put(key, it) } } // TODO("dispose")
    }

    fun adjustTo(map: Map<K, V>) {
        // If any keys should be removed, remove them.
        this.removeIf { it.key !in map }
        // If any values do not match, update them.
        map.filter { (k, v) -> k !in this || v != this[k] }.forEach { (k, v) -> this[k] = v }
    }

    // # Override HashMap functions

    override fun putAll(from: Map<out K, V>) {
        super.putAll(from)
        from.forEach { (key, value) ->
            _itemObservableMap[key]?.also { subject ->
                subject.onNext(value)
                changePublisher.onNext(Change(AddRemEditType.EDIT, key, value))
            } ?: run {
                _itemObservableMap[key] = createItemObservable(key, value)
                changePublisher.onNext(Change(AddRemEditType.ADD, key, value))
            }
        }
        observableMapPublisher.onNext(_itemObservableMap)
    }

    override fun put(key: K, value: V): V? {
        val x = super.put(key, value)
        _itemObservableMap[key]?.also { subject ->
            subject.onNext(value)
            changePublisher.onNext(Change(AddRemEditType.EDIT, key, value))
        } ?: run {
            _itemObservableMap[key] = createItemObservable(key, value)
            changePublisher.onNext(Change(AddRemEditType.ADD, key, value))
        }
        observableMapPublisher.onNext(_itemObservableMap)
        return x
    }

    override fun clear() {
        _itemObservableMap.forEach { (key, subject) ->
            if (exitValue != null) {
                subject.onNext(exitValue)
                changePublisher.onNext(Change(AddRemEditType.EDIT, key, exitValue))
            }
        }
        super.clear()
        val temp = _itemObservableMap.copy()
        _itemObservableMap.clear()
        temp.forEach { (key, subject) ->
            changePublisher.onNext(Change(AddRemEditType.REMOVE, key, subject.value))
        }
        observableMapPublisher.onNext(_itemObservableMap)
    }

    override fun remove(key: K): V? {
        _itemObservableMap[key]?.also { subject ->
            if (exitValue != null) {
                subject.onNext(exitValue)
                changePublisher.onNext(Change(AddRemEditType.EDIT, key, exitValue))
            }
        }
        val x = super.remove(key)
        _itemObservableMap[key]?.also { subject ->
            _itemObservableMap.remove(key)
            changePublisher.onNext(Change(AddRemEditType.REMOVE, key, subject.value))
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
