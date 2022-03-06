package com.tminus1010.budgetvalue._core.framework.source_objects

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.coroutines.rx3.asFlow

class SourceList<T> : ArrayList<T>() {
    private val behaviorSubject = BehaviorSubject.createDefault<List<T>>(emptyList())
    val observable: Observable<List<T>> = behaviorSubject
    val flow = behaviorSubject.asFlow()

    override fun clear() {
        super.clear()
        behaviorSubject.onNext(this)
    }

    override fun addAll(elements: Collection<T>): Boolean {
        val x = super.addAll(elements)
        behaviorSubject.onNext(this)
        return x
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        val x = super.addAll(index, elements)
        behaviorSubject.onNext(this)
        return x
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        val x = super.removeAll(elements)
        behaviorSubject.onNext(this)
        return x
    }

    override fun add(element: T): Boolean {
        val x = super.add(element)
        behaviorSubject.onNext(this)
        return x
    }

    override fun add(index: Int, element: T) {
        super.add(index, element)
        behaviorSubject.onNext(this)
    }

    override fun removeAt(index: Int): T {
        val x = super.removeAt(index)
        behaviorSubject.onNext(this)
        return x
    }

    fun takeLast(): T? {
        val x =
            try {
                this.removeLast()
            } catch (e: NoSuchElementException) {
                null
            }
        behaviorSubject.onNext(this)
        return x
    }

    override fun remove(element: T): Boolean {
        val x = super.remove(element)
        behaviorSubject.onNext(this)
        return x
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        val x = super.retainAll(elements)
        behaviorSubject.onNext(this)
        return x
    }
}