package com.tminus1010.budgetvalue._core.extensions

import com.tminus1010.budgetvalue._core.middleware.source_objects.SourceHashMap
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.plusAssign
import io.reactivex.rxjava3.subjects.Subject
import java.util.concurrent.Semaphore

fun <K, V, T> Observable<Map<K, V>>.flatMapSourceHashMap(sourceHashMap: SourceHashMap<K, V> = SourceHashMap(), outputChooser: (SourceHashMap<K, V>) -> Observable<T>): Observable<T> =
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

fun <T> Observable<T>.divertErrors(errorSubject: Subject<Throwable>): Observable<T> =
    this.onErrorResumeNext { errorSubject.onNext(it); Observable.empty() }

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