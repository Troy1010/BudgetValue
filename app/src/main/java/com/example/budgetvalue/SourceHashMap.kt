package com.example.budgetvalue

import com.tminus1010.tmcommonkotlin_rx.toBehaviorSubject
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.ReplaySubject

class SourceHashMap<T, V> : HashMap<T, V>() {
    private val innerObservable = ReplaySubject.create<SourceHashMap<T, V>>()
    /**
     * this observable emits whenever SourceHashMap puts or removes.
     * It emits a HashMap of T : BehaviorSubject<V>
     */
    // # Bind Map -> ObservableMap
    val observable: BehaviorSubject<HashMap<T, BehaviorSubject<V>>> = innerObservable
        .scan(HashMap()) { observableMap:HashMap<T, BehaviorSubject<V>>, map:HashMap<T, V> ->
            val keysToRemove = arrayListOf<T>()
            for (observableMapKey in observableMap.keys.asSequence().filter { it !in map.keys }) {
                keysToRemove.add(observableMapKey)
            }
            keysToRemove.forEach { observableMap.remove(it) }
            for (mapKey in map.keys.asSequence().filter { it !in observableMap.keys }) {
                observableMap[mapKey] = createItemObservable(mapKey)
            }
            observableMap
        }.toBehaviorSubject()

    fun createItemObservable(key:T): BehaviorSubject<V> {
        return BehaviorSubject.createDefault(this[key]!!)
            .also {
                // # Bind Map -> ItemObservable
                innerObservable.map { it[key]!! }.distinctUntilChanged().subscribe(it)
                // # Bind ItemObservable -> Map
                it.skip(1).subscribe { super.put(key, it) }
            }
    }

    override fun clear() {
        super.clear()
        innerObservable.onNext(this)
    }

    override fun putAll(from: Map<out T, V>) {
        super.putAll(from)
        innerObservable.onNext(this)
    }

    override fun put(key: T, value: V): V? {
        val x = super.put(key, value)
        innerObservable.onNext(this)
        return x
    }

    override fun remove(key: T): V? {
        val x = super.remove(key)
        innerObservable.onNext(this)
        return x
    }
}