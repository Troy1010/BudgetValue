package com.example.budgetvalue.util

import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.PublishSubject

class ObservableArrayList<T> : ArrayList<T>() {
    val observable = PublishSubject.create<Unit>()

    override fun clear() {
        super.clear()
        observable.onNext(Unit)
    }

    override fun addAll(elements: Collection<T>): Boolean {
        val x = super.addAll(elements)
        observable.onNext(Unit)
        return x
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        val x = super.addAll(index, elements)
        observable.onNext(Unit)
        return x
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        val x = super.removeAll(elements)
        observable.onNext(Unit)
        return x
    }

    override fun add(element: T): Boolean {
        val x = super.add(element)
        observable.onNext(Unit)
        return x
    }

    override fun add(index: Int, element: T) {
        super.add(index, element)
        observable.onNext(Unit)
    }

    override fun removeAt(index: Int): T {
        val x = super.removeAt(index)
        observable.onNext(Unit)
        return x
    }

    override fun remove(element: T): Boolean {
        val x = super.remove(element)
        observable.onNext(Unit)
        return x
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        val x = super.retainAll(elements)
        observable.onNext(Unit)
        return x
    }
}