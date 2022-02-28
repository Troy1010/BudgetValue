package com.tminus1010.budgetvalue._core.all.extensions

import com.tminus1010.budgetvalue._core.domain.CategoryAmountFormulas
import com.tminus1010.budgetvalue._core.domain.CategoryAmounts
import com.tminus1010.budgetvalue._core.framework.ColdObservable
import com.tminus1010.budgetvalue._core.framework.source_objects.SourceHashMap
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.transactions.app.AmountFormula
import com.tminus1010.tmcommonkotlin.rx.extensions.value
import com.tminus1010.tmcommonkotlin.tuple.Box
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.plusAssign
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.Subject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.rx3.asFlow
import kotlinx.coroutines.rx3.asObservable
import java.math.BigDecimal
import java.util.concurrent.Semaphore

@JvmName("flatMapSourceHashMap2")
fun <T> Observable<CategoryAmounts>.flatMapSourceHashMap(sourceHashMap: SourceHashMap<Category, BigDecimal> = SourceHashMap(), outputChooser: (SourceHashMap<Category, BigDecimal>) -> Observable<T>): Observable<T> =
    map { it.toMap() }.flatMapSourceHashMap(sourceHashMap, outputChooser)

@JvmName("flatMapSourceHashMap3")
fun <T> Observable<CategoryAmountFormulas>.flatMapSourceHashMap(sourceHashMap: SourceHashMap<Category, AmountFormula> = SourceHashMap(), outputChooser: (SourceHashMap<Category, AmountFormula>) -> Observable<T>): Observable<T> =
    map { it.toMap() }.flatMapSourceHashMap(sourceHashMap, outputChooser)

fun <K, V: Any, T> Observable<Map<K, V>>.flatMapSourceHashMap(sourceHashMap: SourceHashMap<K, V> = SourceHashMap(), outputChooser: (SourceHashMap<K, V>) -> Observable<T>): Observable<T> =
    compose { upstream ->
        Observable.create<T> { downstream ->
            CompositeDisposable(
                outputChooser(sourceHashMap)
                    .subscribe({ downstream.onNext(it) }, { downstream.onError(it) }),
                upstream
                    .subscribe({ sourceHashMap.adjustTo(it) },
                        { downstream.onError(it) },
                        { downstream.onComplete() })
            ).also { downstream.setDisposable(it) }
        }
    }

// TODO: Rewrite
fun <K, V: Any, T : Any> Flow<Map<K, V>>.flatMapSourceHashMap(sourceHashMap: SourceHashMap<K, V> = SourceHashMap(), outputChooser: (SourceHashMap<K, V>) -> Flow<T>): Flow<T> =
    asObservable().flatMapSourceHashMap(sourceHashMap) { sourceHashMap -> outputChooser(sourceHashMap).asObservable() }.asFlow()

fun <K, V: Any> Observable<Map<K, V>>.toSourceHashMap(disposables: CompositeDisposable, sourceHashMap: SourceHashMap<K, V> = SourceHashMap()): SourceHashMap<K, V> =
    this
        .subscribeBy(onNext = { sourceHashMap.adjustTo(it) })
        .also { disposables += it }
        .let { sourceHashMap }

fun <T> Observable<T>.divertErrors(errorSubject: Subject<Throwable>): Observable<T> =
    Observable.defer { onErrorResumeNext { errorSubject.onNext(it); Observable.empty() } }

private class NonLazyCacheHelper<T>(source: Observable<T>, compositeDisposable: CompositeDisposable) {
    private val semaphore = Semaphore(1)
    private var cache: Observable<T>? = null
    val cacheOrSource: Observable<T> =
        Observable.defer {
            semaphore.acquireUninterruptibly()
            (cache ?: source
                .doOnError { cache = null }
                .replay(1).refCount()
                .also { cache = it })
                .also { semaphore.release() }
        }
            .also { compositeDisposable += it.subscribe({}, {}) }
}

fun <T> Observable<T>.nonLazyCache(compositeDisposable: CompositeDisposable): Observable<T> =
    NonLazyCacheHelper(this, compositeDisposable).cacheOrSource

val <T : Any> Observable<Box<T?>>.unbox: T
    get() = this.value!!.first!!

fun <T : Any> Observable<T>.cold(): ColdObservable<T> =
    ColdObservable(this)

fun <T : Any, D : Any> Observable<T>.mapBox(lambda: (T) -> D?): Observable<Box<D?>> =
    map { Box(lambda(it)) }

fun <T : Any, D : Any> Observable<T>.mapNotNull(lambda: (T) -> D?): Observable<D> {
    return this.flatMap { lambda(it)?.let { Observable.just(it) } ?: Observable.empty() }
}
