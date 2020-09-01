package com.example.budgetvalue.util

import android.annotation.SuppressLint
import android.content.res.Resources
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tmcommonkotlin.logz
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableSource
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject

fun <T> ObservableSource<T>.toLiveData2(): LiveData<T> {
    return convertRXToLiveData2(this)
}

fun <T> convertRXToLiveData2(observable: ObservableSource<T>): LiveData<T> {
    return LiveDataReactiveStreams.fromPublisher(
        Flowable.fromObservable(
            observable,
            BackpressureStrategy.DROP
        )
    )
}

// This might be buggy..
fun <T> Observable<T>.toBehaviorSubject(): BehaviorSubject<T> {
    val behaviorSubject = BehaviorSubject.create<T>()
    this.subscribe(behaviorSubject)
    return behaviorSubject
}

fun <A, B, C, D, E> combineLatestAsTuple(
    a: ObservableSource<A>,
    b: ObservableSource<B>,
    c: ObservableSource<C>,
    d: ObservableSource<D>,
    e: ObservableSource<E>
): Observable<Quintuple<A, B, C, D, E>> {
    return Observable.combineLatest(
        listOf(a, b, c, d, e)
    ) {
        Quintuple(
            it[0] as A,
            it[1] as B,
            it[2] as C,
            it[3] as D,
            it[4] as E
        )
    }
}
fun <A, B, C, D> combineLatestAsTuple(
    a: ObservableSource<A>,
    b: ObservableSource<B>,
    c: ObservableSource<C>,
    d: ObservableSource<D>
): Observable<Quadruple<A, B, C, D>> {
    return Observable.combineLatest(
        listOf(a, b, c, d)
    ) {
        Quadruple(
            it[0] as A,
            it[1] as B,
            it[2] as C,
            it[3] as D
        )
    }
}
fun <A, B, C> combineLatestAsTuple(
    a: ObservableSource<A>,
    b: ObservableSource<B>,
    c: ObservableSource<C>
): Observable<Triple<A, B, C>> {
    return Observable.combineLatest(
        listOf(a, b, c)
    ) {
        Triple(
            it[0] as A,
            it[1] as B,
            it[2] as C
        )
    }
}

fun <A, B> combineLatestAsTuple(a: ObservableSource<A>, b: ObservableSource<B>): Observable<Pair<A, B>> {
    return Observable.combineLatest(
        listOf(a, b)
    ) {
        Pair(
            it[0] as A,
            it[1] as B
        )
    }
}



fun <T> LiveData<T>.observeOnce(action: (T?) -> Unit) {
    this.value
    val observer = object : Observer<T> {
        override fun onChanged(o: T?) {
            this@observeOnce.removeObserver(this)
            action(o)
        }
    }
    this.observeForever(observer)
}

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, action: (T?) -> Unit) {
    this.value
    val observer = object : Observer<T> {
        override fun onChanged(o: T?) {
            this@observeOnce.removeObserver(this)
            action(o)
        }
    }
    this.observe(lifecycleOwner, observer)
}

fun <T, R> Iterable<T>.zipWithDefault(other: Iterable<R>, default: R): List<Pair<T, R>> {
    val first = iterator()
    val second = other.iterator()
    val list = ArrayList<Pair<T, R>>()
    while (first.hasNext()) {
        val y = if (second.hasNext()) {
            second.next()
        } else {
            default
        }
        list.add(Pair(first.next(), y))
    }
    return list
}

fun View.setDimToWrapContent() {
    val wrapSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    this.measure(wrapSpec, wrapSpec)
    this.layoutParams = RecyclerView.LayoutParams(this.measuredWidth, this.measuredHeight)
}

fun generateLipsum(size: Int): List<String> {
    val alphabet = "abcdefghijklmnopqrstuvwxyz"
    val returning = ArrayList<String>()
    for (i in 0 until size) {
        var s = ""
        val sizeOfWord = (4..30).random()
        for (j in 0 until sizeOfWord) {
            s+=alphabet.random()
        }
        returning.add(s)
    }
    return returning.toList()
}

fun generateLipsum(): String {
    return generateLipsum(1)[0]
}



fun PublishSubject<Unit>.onNext() {
    this.onNext(Unit)
}

@SuppressLint("CheckResult")
fun <T> Observable<T>.logSubscribe2(msgPrefix: String? = null, bType: Boolean = false): Observable<T> {
    val tempMsgPrefix: String = if (msgPrefix == null) "" else {
        "$msgPrefix`"
    }
    this
        .subscribe({
            if (bType) {
                val typeName = if (it == null) {
                    "null"
                } else {
                    (it as Any)::class.java.simpleName
                }
                logz("$tempMsgPrefix$typeName`$it")
            } else {
                logz("$tempMsgPrefix$it")
            }
        }, {
            logz("${tempMsgPrefix}Error`$it")
        })
    return this
}

val GridLayoutManager.visibleChildren: HashMap<Int, View>
    get() {
        val children = HashMap<Int, View>()
        for (childIndex in this.findFirstVisibleItemPosition()..this.findLastVisibleItemPosition()) {
            val child = this.getChildAt(childIndex)
            if (child==null) {
                logz("Warning`GridLayoutManager.visibleChildren`child was null at position:${childIndex}")
                logz("this.findFirstVisibleItemPosition():${this.findFirstVisibleItemPosition()}, this.findLastVisibleItemPosition():${this.findLastVisibleItemPosition()}")
            } else {
                children[childIndex] = child
            }
        }
        return children
    }



val View.measuredWidth2 : Int
    get() {
        this.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        return this.measuredWidth
    }

fun getScreenWidth(): Int {
    return Resources.getSystem().displayMetrics.widthPixels
}

fun getScreenHeight(): Int {
    return Resources.getSystem().displayMetrics.heightPixels
}
