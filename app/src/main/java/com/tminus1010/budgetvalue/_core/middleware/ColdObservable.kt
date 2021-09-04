package com.tminus1010.budgetvalue._core.middleware

import io.reactivex.rxjava3.core.ObservableSource
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable

/**
 * Cold observables always have at least 1 value available, either from caching or an internal producer.
 *
 * This class does not guarantee that it wraps a
 */
class ColdObservable<T : Any>(observable: ObservableSource<T>) : ObservableSource<T> by observable {
    val value: T
        get() {
            var returning: T? = null
            var error: Throwable? = null
            var disposable: Disposable? = null
            subscribe(object : Observer<T> {
                override fun onSubscribe(d: Disposable?) {
                    disposable = d
                }

                override fun onNext(it: T) {
                    returning = it
                }

                override fun onError(it: Throwable?) {
                    error = it
                }

                override fun onComplete() {
                }

            })
            disposable!!.dispose()
            error?.also { throw it }
            return returning!!
        }
}