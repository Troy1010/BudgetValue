package com.tminus1010.budgetvalue.extensions

import com.tminus1010.budgetvalue.middleware.source_objects.SourceHashMap
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

fun <T> Observable<T>.io(): Observable<T> = observeOn(Schedulers.io())
fun <T> Observable<T>.launch(scheduler: Scheduler = Schedulers.io(), completableProvider: (T) -> Completable): Disposable =
    this.observeOn(scheduler).flatMapCompletable { completableProvider(it) }.subscribe()

fun <K, V, T: SourceHashMap<K, V>> Observable<T>.itemObservableMap2() = // TODO("there must be a better way..")
    take(1).flatMap { it.itemObservableMap2 }

fun <A, B> Observable<A>.withLatestFrom2(o1: Observable<B>) =
    this.withLatestFrom(o1) { a, b -> Pair(a, b) }

fun <T> Observable<T>.toCompletable() = Completable.fromObservable(this)

fun <K, V, T> Observable<Map<K, V>>.toSourceHashMapOutput(sourceHashMap: SourceHashMap<K, V>, outputChooser: (SourceHashMap<K, V>) -> Observable<T>) =
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