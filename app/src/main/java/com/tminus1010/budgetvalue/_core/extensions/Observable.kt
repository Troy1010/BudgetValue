package com.tminus1010.budgetvalue._core.extensions

import androidx.lifecycle.LiveDataReactiveStreams
import com.tminus1010.budgetvalue._core.middleware.source_objects.SourceHashMap
import com.tminus1010.tmcommonkotlin.rx.extensions.value
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.Subject

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

fun <T> Observable<T>.divertErrors(errorSubject: Subject<Throwable>) =
    this.onErrorResumeNext { errorSubject.onNext(it); Observable.empty() }

private fun <T> Observable<T>.toLiveData() =
    LiveDataReactiveStreams.fromPublisher(this.toFlowable(BackpressureStrategy.LATEST))

fun <T> Observable<T>.toLiveData(errorSubject: Subject<Throwable>) =
    divertErrors(errorSubject).toLiveData()

fun <T> Observable<T>.nonLazyCache(compositeDisposable: CompositeDisposable) =
    replay(1).also { compositeDisposable.add(it.connect()) }

fun <T> Observable<T>.await() = value ?: take(1).blockingLast()

/**
 * Emits the first onNext().
 */
fun <T> Observable<T>.toSingle(): Single<T> =
    Single.fromObservable(take(1))