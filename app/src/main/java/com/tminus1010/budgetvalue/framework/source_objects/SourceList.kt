package com.tminus1010.budgetvalue.framework.source_objects

import com.tminus1010.tmcommonkotlin.coroutines.extensions.pairwise
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.rx3.asFlow
import java.lang.Integer.max

class SourceList<T>(iterable: Iterable<T> = emptyList()) : ArrayList<T>() {
    constructor(vararg values: T) : this(values.toList())

    private val behaviorSubject = BehaviorSubject.createDefault(this)

    init {
        adjustTo(iterable.toList())
    }

    val observable: Observable<SourceList<T>> = behaviorSubject
    val flow = behaviorSubject.asFlow()
    val onAddOrRemove = flow.onStart { emit(SourceList()) }.pairwise().filter { it.first.size != it.second.size }.map { it.second }

    fun adjustTo(list: List<T>) {
        // If there are too many items, remove some.
        this.removeAll(this.takeLast(max(0, this.size - list.size)))
        // If any items don't match, reassign them
        list.withIndex().forEach { (i, v) ->
            if (i !in this.indices)
                this.add(i, v)
            else if (this[i] != v)
                this[i] = v
        }
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

    // # ArrayList Overrides
    override fun set(index: Int, element: T): T {
        val x = super.set(index, element)
        behaviorSubject.onNext(this)
        return x
    }

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