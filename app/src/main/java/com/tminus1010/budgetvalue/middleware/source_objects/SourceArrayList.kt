package com.tminus1010.budgetvalue.middleware.source_objects

import io.reactivex.rxjava3.subjects.PublishSubject

class SourceArrayList<T> : ArrayList<T>() {
    val observable = PublishSubject.create<ArrayList<T>>()

    override fun clear() {
        super.clear()
        observable.onNext(this)
    }

    override fun addAll(elements: Collection<T>): Boolean {
        val x = super.addAll(elements)
        observable.onNext(this)
        return x
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        val x = super.addAll(index, elements)
        observable.onNext(this)
        return x
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        val x = super.removeAll(elements)
        observable.onNext(this)
        return x
    }

    override fun add(element: T): Boolean {
        val x = super.add(element)
        observable.onNext(this)
        return x
    }

    override fun add(index: Int, element: T) {
        super.add(index, element)
        observable.onNext(this)
    }

    override fun removeAt(index: Int): T {
        val x = super.removeAt(index)
        observable.onNext(this)
        return x
    }

    override fun remove(element: T): Boolean {
        val x = super.remove(element)
        observable.onNext(this)
        return x
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        val x = super.retainAll(elements)
        observable.onNext(this)
        return x
    }
}