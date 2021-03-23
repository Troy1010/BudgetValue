@file:Suppress("NAME_SHADOWING")

package com.tminus1010.budgetvalue.middleware

import android.content.res.Resources
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tminus1010.budgetvalue.features.transactions.Transaction
import com.tminus1010.tmcommonkotlin.misc.logz
import com.tminus1010.tmcommonkotlin.rx.extensions.boxStartNull
import com.tminus1010.tmcommonkotlin.rx.extensions.isCold
import com.tminus1010.tmcommonkotlin.misc.extensions.previous
import com.tminus1010.tmcommonkotlin.tuple.*
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableSource
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.functions.Function3
import io.reactivex.rxjava3.functions.Function4
import java.io.PrintWriter
import java.io.StringWriter
import java.lang.reflect.Type
import java.math.BigDecimal
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

fun Iterable<Transaction>.getBlocks(numOfWeeks: Int): HashMap<LocalDate, java.util.ArrayList<Transaction>> {
    val transactionBlocks = HashMap<LocalDate, java.util.ArrayList<Transaction>>()
    for (transaction in this) {
        if (transactionBlocks[transaction.date.previous(DayOfWeek.MONDAY)] == null)
            transactionBlocks[transaction.date.previous(DayOfWeek.MONDAY)] = arrayListOf()
        transactionBlocks[transaction.date.previous(DayOfWeek.MONDAY)]!!.add(transaction)
    }
    //
    var i = 0
    var mmm = arrayListOf<Transaction>()
    var keysToRemove = arrayListOf<LocalDate>()
    for (x in transactionBlocks.toSortedMap(compareBy { it })) {
        val transactions = x.value
        if (i == 0) {
            mmm = transactions
        } else {
            keysToRemove.add(x.key)
            mmm.addAll(transactions)
        }
        i = (i + 1) % numOfWeeks
    }
    for (key in keysToRemove) {
        transactionBlocks.remove(key)
    }
    return transactionBlocks
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
            s += alphabet.random()
        }
        returning.add(s)
    }
    return returning.toList()
}

fun generateLipsum(): String {
    return generateLipsum(1)[0]
}

val GridLayoutManager.visibleChildren: HashMap<Int, View>
    get() {
        val children = HashMap<Int, View>()
        for (childIndex in this.findFirstVisibleItemPosition()..this.findLastVisibleItemPosition()) {
            val child = this.getChildAt(childIndex)
            if (child == null) {
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

val View.intrinsicHeight2: Int
    get() {
        this.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        return this.measuredHeight
    }

fun View.measureUnspecified() {
    this.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
}

fun View.measureExact() {
    this.measure(View.MeasureSpec.EXACTLY, View.MeasureSpec.EXACTLY)
}

val View.intrinsicWidth2: Int
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
        Orientation.VERTICAL -> {
            for (collection in z2dCollection) {
                returning.addAll(collection)
            }
        }
        Orientation.HORIZONTAL -> {
            for (i in 0 until (z2dCollection.map { it.size }.maxOrNull() ?: 0)) {
                for (collection in z2dCollection) {
                    returning.add(collection.getOrNull(i))
                }
            }
        }
    }
    return returning.toList()
}

fun <T> generate2dArrayList(
    xSize: Int,
    ySize: Int,
    orientation: Orientation,
): ArrayList<ArrayList<T?>> {
    val returning = ArrayList<ArrayList<T?>>()
    when (orientation) {
        Orientation.HORIZONTAL -> {
            for (yPos in 0 until ySize) {
                returning.add(ArrayList())
                for (xPos in 0 until xSize) {
                    returning[yPos].add(null)
                }
            }
        }
        Orientation.VERTICAL -> {
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


fun <T> Iterable<Iterable<T>>.reflectXY(): ArrayList<ArrayList<T>> {
    return this.map { it.toList() }.toList().reflectXY()
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

fun logThread(prefix: Any?) =
    logz("$prefix. Thread:${Thread.currentThread().name}")

fun <T, V> List<HashMap<T, V>>.reflectXY(): HashMap<T, ArrayList<V>> {
    val returning = HashMap<T, ArrayList<V>>()
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


fun String.toBigDecimalSafe(): BigDecimal =
    toBigDecimalOrNull() ?: BigDecimal.ZERO

fun String.toMoneyBigDecimal(): BigDecimal =
    toBigDecimalSafe()
        .let { if (it.scale() == 1) it.setScale(2) else it }
        .let { try { it.setScale(0) } catch (e: Throwable) { it } } // throws error if decimal digits are not zeros.

fun BigDecimal.nullIfZero(): BigDecimal? =
    if (this.compareTo(BigDecimal.ZERO) == 0) null else this

fun <A, B> zip(a: ObservableSource<A>, b: ObservableSource<B>): Observable<Pair<A, B>> {
    return Observable.zip(a, b, BiFunction<A, B, Pair<A, B>> { a, b -> Pair(a, b) })
}

fun <A, B, C> zip(
    a: ObservableSource<A>,
    b: ObservableSource<B>,
    c: ObservableSource<C>,
): Observable<Triple<A, B, C>> {
    return Observable.zip(a,
        b,
        c,
        Function3<A, B, C, Triple<A, B, C>> { a, b, c -> Triple(a, b, c) })
}

fun <A, B, C, D> zip(
    a: ObservableSource<A>,
    b: ObservableSource<B>,
    c: ObservableSource<C>,
    d: ObservableSource<D>,
): Observable<Quadruple<A, B, C, D>> {
    return Observable.zip(a, b, c, d, Function4<A, B, C, D, Quadruple<A, B, C, D>> { a, b, c, d ->
        Quadruple(
            a,
            b,
            c,
            d)
    })
}

data class IndexAndTuple<T>(
    val index: Int,
    val tuple: T,
)

data class TypeAndValue(
    val type: Type,
    val tuple: Any,
)

fun <A, B> combineLatestWithIndex(
    a: Observable<A>,
    b: Observable<B>,
): Observable<Triple<Int, A, B>> {
    return Observable.merge(a.map { 0 }, b.map { 1 })
        .zipWith(Rx.combineLatest(a, b)) { index, tuple -> IndexAndTuple(index, tuple) }
        .map { Triple(it.index, it.tuple.first, it.tuple.second) }
}

fun <A, B, C> combineLatestWithIndex(
    a: Observable<A>,
    b: Observable<B>,
    c: Observable<C>,
): Observable<Quadruple<Int, A, B, C>> {
    return Observable.zip(
        Observable.merge(a.map { 0 }, b.map { 1 }, c.map { 2 }),
        Rx.combineLatest(a, b, c)
    ) { index, tuple -> IndexAndTuple(index, tuple) }
        .map { Quadruple(it.index, it.tuple.first, it.tuple.second, it.tuple.third) }
}

@Suppress("UNCHECKED_CAST")
fun <A, B> mergeCombineWithIndex(
    a: Observable<A>,
    b: Observable<B>,
): Observable<Triple<Int, A?, B?>> {
    return Observable.zip(
        Observable.merge(a.map { 0 }, b.map { 1 }),
        Observable.merge(a, b)
    ) { i, v -> Pair(i, v) }
        .scan(Triple<Int, A?, B?>(-1, null, null)) { acc, (i, v) ->
            when (i) {
                0 -> acc.copy(first = 0, second = v as A?)
                1 -> acc.copy(first = 1, third = v as B?)
                else -> error("Unhandled i:$i")
            }
        }
        .skip(1)
}

@Suppress("UNCHECKED_CAST")
fun <A, B, C> mergeCombineWithIndex(
    a: Observable<A>,
    b: Observable<B>,
    c: Observable<C>,
): Observable<Quadruple<Int, A?, B?, C?>> {
    return Observable.zip(
        Observable.merge(a.map { 0 }, b.map { 1 }, c.map { 2 }),
        Observable.merge(a, b, c)
    ) { i, v -> Pair(i, v) }
        .scan(Quadruple<Int, A?, B?, C?>(-1, null, null, null)) { acc, (i, v) ->
            when (i) {
                0 -> acc.copy(first = 0, second = v as A?)
                1 -> acc.copy(first = 1, third = v as B?)
                2 -> acc.copy(first = 2, fourth = v as C?)
                else -> error("Unhandled i:$i")
            }
        }
        .skip(1)
}

@Suppress("UNCHECKED_CAST")
fun <A, B, C, D> mergeCombineWithIndex(
    a: Observable<A>,
    b: Observable<B>,
    c: Observable<C>,
    d: Observable<D>,
): Observable<Quintuple<Int, A?, B?, C?, D?>> {
    return Observable.zip(
        Observable.merge(a.map { 0 }, b.map { 1 }, c.map { 2 }, d.map { 3 }),
        Observable.merge(a, b, c, d)
    ) { i, v -> Pair(i, v) }
        .scan(Quintuple<Int, A?, B?, C?, D?>(-1, null, null, null, null)) { acc, (i, v) ->
            when (i) {
                0 -> acc.copy(first = 0, second = v as A?)
                1 -> acc.copy(first = 1, third = v as B?)
                2 -> acc.copy(first = 2, fourth = v as C?)
                3 -> acc.copy(first = 3, fifth = v as D?)
                else -> error("Unhandled i:$i")
            }
        }
        .skip(1)
}

fun <A, B, C> combineLatestImpatient(
    a: Observable<A>,
    b: Observable<B>,
    c: Observable<C>,
): Observable<Triple<A?, B?, C?>> {
    return Rx.combineLatest(a.boxStartNull(), b.boxStartNull(), c.boxStartNull())
        .compose { observable ->
            // # If no observables are cold, then skip the first emission
            // * The observables start with null so that combineLatest is impatient.
            //   However, we cannot assume that the first emission will be that initial skippable
            //   tuple of nulls, because cold observables will emit their latest value even at the
            //   first emission.
            if (listOf(a, b, c).none { it.isCold() }) {
                observable.skip(1)
            } else observable
        }
        .map { Triple(it.first.unbox(), it.second.unbox(), it.third.unbox()) }
}

fun <A, B, C, D> combineLatestImpatient(
    a: Observable<A>,
    b: Observable<B>,
    c: Observable<C>,
    d: Observable<D>,
): Observable<Quadruple<A?, B?, C?, D?>> {
    return Rx.combineLatest(a.boxStartNull(), b.boxStartNull(), c.boxStartNull(), d.boxStartNull())
        .compose { observable ->
            // # If no observables are cold, then skip the first emission
            // * The observables start with null so that combineLatest is impatient.
            //   However, we cannot assume that the first emission will be that initial skippable
            //   tuple of nulls, because cold observables will emit their latest value even at the
            //   first emission.
            if (listOf(a, b, c, d).none { it.isCold() }) {
                observable.skip(1)
            } else observable
        }
        .map { Quadruple(it.first.unbox(), it.second.unbox(), it.third.unbox(), it.fourth.unbox()) }
}

fun <A, B, C, D, E> combineLatestImpatient(
    a: Observable<A>,
    b: Observable<B>,
    c: Observable<C>,
    d: Observable<D>,
    e: Observable<E>,
): Observable<Quintuple<A?, B?, C?, D?, E?>> {
    return Rx.combineLatest(
        a.boxStartNull(),
        b.boxStartNull(),
        c.boxStartNull(),
        d.boxStartNull(),
        e.boxStartNull()
    )
        .compose { observable ->
            // # If no observables are cold, then skip the first emission
            // * The observables start with null so that combineLatest is impatient.
            //   However, we cannot assume that the first emission will be that initial skippable
            //   tuple of nulls, because cold observables will emit their latest value even at the
            //   first emission.
            if (listOf(a, b, c, d, e).none { it.isCold() }) {
                observable.skip(1)
            } else observable
        }
        .map { Quintuple(it.first.unbox(), it.second.unbox(), it.third.unbox(), it.fourth.unbox(), it.fifth.unbox()) }
}

fun <A, B, C, D, E, F> combineLatestImpatient(
    a: Observable<A>,
    b: Observable<B>,
    c: Observable<C>,
    d: Observable<D>,
    e: Observable<E>,
    f: Observable<F>,
): Observable<Sextuple<A?, B?, C?, D?, E?, F?>> {
    return Rx.combineLatest(
        a.boxStartNull(),
        b.boxStartNull(),
        c.boxStartNull(),
        d.boxStartNull(),
        e.boxStartNull(),
        f.boxStartNull()
    )
        .compose { observable ->
            // # If no observables are cold, then skip the first emission
            // * The observables start with null so that combineLatest is impatient.
            //   However, we cannot assume that the first emission will be that initial skippable
            //   tuple of nulls, because cold observables will emit their latest value even at the
            //   first emission.
            if (listOf(a, b, c, d, e, f).none { it.isCold() }) {
                observable.skip(1)
            } else observable
        }
        .map { Sextuple(it.first.unbox(), it.second.unbox(), it.third.unbox(), it.fourth.unbox(), it.fifth.unbox(), it.sixth.unbox()) }
}

fun <A, B, C, D, E, F, G> combineLatestImpatient(
    a: Observable<A>,
    b: Observable<B>,
    c: Observable<C>,
    d: Observable<D>,
    e: Observable<E>,
    f: Observable<F>,
    g: Observable<G>,
): Observable<Septuple<A?, B?, C?, D?, E?, F?, G?>> {
    return Rx.combineLatest(
        a.boxStartNull(),
        b.boxStartNull(),
        c.boxStartNull(),
        d.boxStartNull(),
        e.boxStartNull(),
        f.boxStartNull(),
        g.boxStartNull()
    )
        .compose { observable ->
            // # If no observables are cold, then skip the first emission
            // * The observables start with null so that combineLatest is impatient.
            //   However, we cannot assume that the first emission will be that initial skippable
            //   tuple of nulls, because cold observables will emit their latest value even at the
            //   first emission.
            if (listOf(a, b, c, d, e, f, g).none { it.isCold() }) {
                observable.skip(1)
            } else observable
        }
        .map { Septuple(it.first.unbox(), it.second.unbox(), it.third.unbox(), it.fourth.unbox(), it.fifth.unbox(), it.sixth.unbox(), it.seventh.unbox()) }
}

fun <T> Box<T>.unbox(): T {
    return this.first
}

val <T> Box<T>.unbox
    get() = this.first

// untested
fun <K, V> createMapEntry(key: K, value: V): Map.Entry<K, V> {
    return object: Map.Entry<K, V> {
        override val key: K
            get() = key
        override val value: V
            get() = value
    }
}
