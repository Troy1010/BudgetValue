package com.tminus1010.budgetvalue._core.extensions

import androidx.lifecycle.LiveDataReactiveStreams
import com.tminus1010.budgetvalue._core.middleware.source_objects.SourceHashMap
import com.tminus1010.tmcommonkotlin.rx.extensions.isCold
import com.tminus1010.tmcommonkotlin.rx.extensions.value
import io.reactivex.rxjava3.core.*
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.Subject

fun <T> Observable<T>.io(): Observable<T> = observeOn(Schedulers.io())
fun <T> Observable<T>.launch(scheduler: Scheduler = Schedulers.io(), completableProvider: (T) -> Completable): Disposable =
    observeOn(scheduler).flatMapCompletable { completableProvider(it) }.subscribe()

fun <K, V, T: SourceHashMap<K, V>> Observable<T>.itemObservableMap2() = // TODO("there must be a better way..")
    take(1).flatMap { it.itemObservableMap2 }

fun <A, B> Observable<A>.withLatestFrom2(o1: Observable<B>) =
    this.withLatestFrom(o1) { a, b -> Pair(a, b) }

fun <T> Observable<T>.toCompletable() = Completable.fromObservable(this)

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

@Deprecated("loses disposable")
fun <T> Observable<T>.nonLazyCache() =
    replay(1).also { it.connect() }

fun <T> Observable<T>.await() = value ?: take(1).blockingLast()

/**
 * Emits the first onNext().
 */
fun <T> Observable<T>.toSingle(): Single<T> =
    Single.fromObservable(take(1))