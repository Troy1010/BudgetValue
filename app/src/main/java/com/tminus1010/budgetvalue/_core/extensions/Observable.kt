package com.tminus1010.budgetvalue._core.extensions

import androidx.lifecycle.LiveDataReactiveStreams
import com.tminus1010.budgetvalue._core.middleware.source_objects.SourceHashMap
import com.tminus1010.tmcommonkotlin.rx.extensions.value
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.plusAssign
import io.reactivex.rxjava3.observables.ConnectableObservable
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

private fun <T> Observable<T>.toLiveData() =
    LiveDataReactiveStreams.fromPublisher(this.toFlowable(BackpressureStrategy.LATEST))

fun <T> Observable<T>.toLiveData(errorSubject: Subject<Throwable>) =
    divertErrors(errorSubject).toLiveData()

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

/**
 * Emits the first onNext().
 */
fun <T> Observable<T>.toSingle(): Single<T> =
    Single.fromObservable(take(1))