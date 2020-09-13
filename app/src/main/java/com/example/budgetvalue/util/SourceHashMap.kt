package com.example.budgetvalue.util

import io.reactivex.rxjava3.subjects.PublishSubject

class SourceHashMap<T, V> : HashMap<T, V>() {
    val observable = PublishSubject.create<Unit>()
    override fun clear() {
        super.clear()
        observable.onNext(Unit)
    }

    override fun putAll(from: Map<out T, V>) {
        super.putAll(from)
        observable.onNext(Unit)
    }

    override fun put(key: T, value: V): V? {
        val x = super.put(key, value)
        observable.onNext(Unit)
        return x
    }

    override fun remove(key: T): V? {
        val x = super.remove(key)
        observable.onNext(Unit)
        return x
    }
}