package com.example.budgetvalue.util

import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.ReplaySubject

class SourceHashMap<T, V> : HashMap<T, V>() {
    val observable = ReplaySubject.create<HashMap<T, V>>()
    val itemObservables_ = observable
        .filter {
            if (bSkipNext) {
                bSkipNext = false
                false
            } else {
                true
            }
        }
        .scan(HashMap()) { x:HashMap<T, BehaviorSubject<V>>, y:HashMap<T, V> ->
            for (xKey in x.keys) {
                if (xKey !in y.keys) {
                    x.remove(xKey)
                }
            }
            for (yKey in y.keys) {
                if (yKey !in x.keys) {
                    x[yKey] = createItemObservable(yKey)
                }
            }
            x
        }.toBehaviorSubject()

    var bSkipNext = false
    fun createItemObservable(key:T): BehaviorSubject<V> {
        return BehaviorSubject.createDefault(this[key]!!).apply {
            observable.map { it[key]!! }.distinctUntilChanged().subscribe(this)
            skip(1).subscribe {
                bSkipNext = true
                this@SourceHashMap[key] = it
            }
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