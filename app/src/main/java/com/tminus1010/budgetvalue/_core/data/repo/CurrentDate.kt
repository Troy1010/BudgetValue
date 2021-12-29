package com.tminus1010.budgetvalue._core.data.repo

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.tminus1010.budgetvalue._core.all.extensions.cold
import com.tminus1010.tmcommonkotlin.rx.replayNonError
import io.reactivex.rxjava3.core.Observable
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

// TODO("This is untested")
@Singleton
class CurrentDate @Inject constructor(app: Application) {
    private val currentDate =
        Observable.create<LocalDate> { downstream ->
            val broadcastReceiver =
                object : BroadcastReceiver() {
                    override fun onReceive(context: Context, intent: Intent) {
                        downstream.onNext(LocalDate.now())
                    }
                }
            app.registerReceiver(
                broadcastReceiver,
                IntentFilter().apply {
                    addAction(Intent.ACTION_TIME_TICK)
                    addAction(Intent.ACTION_TIMEZONE_CHANGED)
                    addAction(Intent.ACTION_TIME_CHANGED)
                }
            )
            downstream.setCancellable { app.unregisterReceiver(broadcastReceiver) }
        }
            .distinctUntilChanged()
            .startWithItem(LocalDate.now())
            .replayNonError(1)
            .cold()

    operator fun invoke() = currentDate
}