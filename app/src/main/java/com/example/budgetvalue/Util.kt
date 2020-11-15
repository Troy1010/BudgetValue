@file:Suppress("NAME_SHADOWING")

package com.example.budgetvalue

import android.annotation.SuppressLint
import android.content.res.Resources
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetvalue.models.Transaction
import com.tminus1010.tmcommonkotlin.logz
import com.tminus1010.tmcommonkotlin_tuple.Box
import com.tminus1010.tmcommonkotlin_tuple.Quadruple
import com.tminus1010.tmcommonkotlin_tuple.Quintuple
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableSource
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.functions.Function3
import io.reactivex.rxjava3.functions.Function4
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.io.PrintWriter
import java.io.StringWriter
import java.math.BigDecimal
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

fun Iterable<Transaction>.getBlocks(numOfWeeks: Int): HashMap<LocalDate, java.util.ArrayList<Transaction>> {
    val transactionBlocks = HashMap<LocalDate, java.util.ArrayList<Transaction>>()
    for (transaction in this) {
        if (transactionBlocks[transaction.date.previousMonday()]==null)
            transactionBlocks[transaction.date.previousMonday()] = arrayListOf()
        transactionBlocks[transaction.date.previousMonday()]!!.add(transaction)
    }
    //
    var i = 0
    var mmm = arrayListOf<Transaction>()
    var keysToRemove = arrayListOf<LocalDate>()
    for (x in transactionBlocks.toSortedMap(compareBy { it })) {
        val transactions = x.value
        if (i==0) {
            mmm = transactions
        } else {
            keysToRemove.add(x.key)
            mmm.addAll(transactions)
        }
        i = (i + 1)%numOfWeeks
    }
    for (key in keysToRemove) {
        transactionBlocks.remove(key)
    }
    return transactionBlocks
}


fun LocalDate.previousMonday(): LocalDate {
    return this.with(TemporalAdjusters.next(DayOfWeek.MONDAY))
}

fun LocalDate.nextMonday(): LocalDate {
    return this.with(TemporalAdjusters.previous(DayOfWeek.MONDAY))
}


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



// These might be buggy..
fun <T> Observable<T>.toBehaviorSubject(): BehaviorSubject<T> {
    val behaviorSubject = BehaviorSubject.create<T>()
    this.subscribe(behaviorSubject)
    return behaviorSubject
}
fun <T> Observable<T>.toBehaviorSubjectWithDefault(defaultValue: T): BehaviorSubject<T> {
    val behaviorSubject = BehaviorSubject.createDefault(defaultValue)
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

fun <A> combineLatestAsTuple(a: ObservableSource<A>): Observable<Box<A>> {
    return Observable.combineLatest(
        listOf(a)
    ) {
        Box(
            it[0] as A
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

fun arrayListOfZeros(size: Int): ArrayList<Int> {
    val returning = ArrayList<Int>()
    for (i in 0 until size) {
        returning.add(0)
    }
    return returning
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

fun PublishSubject<Unit>.emit() {
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



fun Throwable.narrate(): String {
    val sw = StringWriter()
    this.printStackTrace(PrintWriter(sw))
    return sw.toString()
}

val View.intrinsicHeight2 : Int
    get() {
        this.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        return this.measuredHeight
    }

val View.intrinsicWidth2 : Int
    get() {
        this.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        return this.measuredWidth
    }

val View.exactWidth: Int
    get() {
        this.measure(View.MeasureSpec.EXACTLY, View.MeasureSpec.EXACTLY)
        return this.measuredWidth
    }

fun getExactWidth(x: Any): Int {
    (x as View).measure(View.MeasureSpec.EXACTLY, View.MeasureSpec.EXACTLY)
    return x.measuredWidth
}

fun getScreenWidth(): Int {
    return Resources.getSystem().displayMetrics.widthPixels
}

fun getScreenHeight(): Int {
    return Resources.getSystem().displayMetrics.heightPixels
}

fun <T> make1d(orientation: Orientation, z2dCollection: List<List<T>>): List<T?> {
    val returning = ArrayList<T?>()
    when (orientation) {
        Orientation.Vertical -> {
            for (collection in z2dCollection) {
                returning.addAll(collection)
            }
        }
        Orientation.Horizontal -> {
            for (i in 0 until (z2dCollection.map { it.size }.max() ?: 0)) {
                for (collection in z2dCollection) {
                    returning.add(collection.getOrNull(i))
                }
            }
        }
    }
    return returning.toList()
}

fun <T> generate2dArrayList(xSize: Int, ySize: Int, orientation: Orientation): ArrayList<ArrayList<T?>> {
    val returning = ArrayList<ArrayList<T?>>()
    when (orientation) {
        Orientation.Horizontal -> {
            for (yPos in 0 until ySize) {
                returning.add(ArrayList())
                for (xPos in 0 until xSize) {
                    returning[yPos].add(null)
                }
            }
        }
        Orientation.Vertical -> {
            for (xPos in 0 until xSize) {
                returning.add(ArrayList())
                for (yPos in 0 until ySize) {
                    returning[xPos].add(null)
                }
            }
        }
    }
    return returning
}



fun <K, V> HashMap<K, V>.sortByList(list: List<K>): SortedMap<K, V> {
    return toSortedMap(compareBy { list.indexOf(it) })
}

fun <T> List<List<T>>.reflectXY(): ArrayList<ArrayList<T>> {
    val returning = ArrayList<ArrayList<T>>()
    for (yPos in this.indices) {
        for (xPos in this[yPos].indices) {
            if (xPos >= returning.size) {
                returning.add(ArrayList())
            }
            returning[xPos].add(this[yPos][xPos])
        }
    }
    return returning
}

fun <T,V> List<HashMap<T,V>>.reflectXY(): HashMap<T,ArrayList<V>> {
    val returning = HashMap<T,ArrayList<V>>()
    for (yPos in this.indices) {
        for (xPos in this[yPos].keys) {
            if (returning[xPos] == null)
                returning[xPos] = arrayListOf()
            logz("xPos:${xPos} yPos:${yPos} ..adding:${this[yPos][xPos]}")
            returning[xPos]!!.add(this[yPos][xPos]!!)
        }
    }
    return returning
}



fun <T : Any> Observable<T>.pairwiseDefault(initialValue: T): Observable<Pair<T, T>> {
    var lastValue = initialValue
    return this.map {
        val returning = Pair(lastValue, it)
        lastValue = it
        returning
    }
}



fun String.toBigDecimal2(): BigDecimal {
    return if (this == "") BigDecimal.ZERO else this.toBigDecimal()
}

fun <A, B> zip(a: ObservableSource<A>, b: ObservableSource<B>) : Observable<Pair<A, B>> {
    return Observable.zip(a, b, BiFunction<A, B, Pair<A, B>> { a, b -> Pair(a, b) })
}

fun <A, B, C> zip(a: ObservableSource<A>, b: ObservableSource<B>, c: ObservableSource<C>) : Observable<Triple<A, B, C>> {
    return Observable.zip(a,
        b,
        c,
        Function3<A, B, C, Triple<A, B, C>> { a, b, c -> Triple(a, b, c) })
}

fun <A, B, C, D> zip(
    a: ObservableSource<A>,
    b: ObservableSource<B>,
    c: ObservableSource<C>,
    d: ObservableSource<D>
) : Observable<Quadruple<A, B, C, D>> {
    return Observable.zip(a, b, c, d, Function4<A, B, C, D, Quadruple<A, B, C, D>> { a, b, c, d ->
        Quadruple(
            a,
            b,
            c,
            d)
    })
}
