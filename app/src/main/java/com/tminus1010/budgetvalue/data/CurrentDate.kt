package com.tminus1010.budgetvalue.data

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.rx3.asFlow
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
            .asFlow()
            .stateIn(GlobalScope, SharingStarted.WhileSubscribed(1000), LocalDate.now())

    val flow = currentDate
}