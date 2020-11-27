package com.example.budgetvalue

import com.tminus1010.tmcommonkotlin_rx.toBehaviorSubject
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.ReplaySubject

class SourceHashMap<T, V> : HashMap<T, V>() {
    /**
     * This observable emits whenever SourceHashMap puts or removes.
     * It emits a HashMap of T : V
     */
    val observable = ReplaySubject.create<SourceHashMap<T, V>>()
    /**
     * this observable emits whenever SourceHashMap puts or removes.
     * It emits a HashMap of T : BehaviorSubject<V>
     */
    // # Bind Map -> ObservableMap
    val itemObservablesObservable: BehaviorSubject<HashMap<T, BehaviorSubject<V>>> = observable
        .scan(HashMap()) { x:HashMap<T, BehaviorSubject<V>>, y:HashMap<T, V> ->
            val xKeysToRemove = arrayListOf<T>()
            for (xKey in x.keys) {
                if (xKey !in y.keys) {
                    xKeysToRemove.add(xKey)
                }
            }
            for (xKey in xKeysToRemove) {
                x.remove(xKey)
            }
            for (yKey in y.keys) {
                if (yKey !in x.keys) {
                    x[yKey] = createItemObservable(yKey)
                }
            }
            x
        }.toBehaviorSubject()

    fun createItemObservable(key:T): BehaviorSubject<V> {
        return BehaviorSubject.createDefault(this[key]!!)
            .also {
                // # Bind Map -> ItemObservable
                observable.map { it[key]!! }.distinctUntilChanged().subscribe(it)
                // # Bind ItemObservable -> Map
                it.skip(1).subscribe { super.put(key, it) }
            }
    }

    override fun clear() {
        super.clear()
        observable.onNext(this)
    }

    override fun putAll(from: Map<out T, V>) {
        super.putAll(from)
        observable.onNext(this)
    }

    override fun put(key: T, value: V): V? {
        val x = super.put(key, value)
        observable.onNext(this)
        return x
    }

    override fun remove(key: T): V? {
        val x = super.remove(key)
        observable.onNext(this)
        return x
    }
}