package com.tminus1010.budgetvalue.all.data

import android.app.Application
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.tminus1010.budgetvalue._core.extensions.cold
import com.tminus1010.tmcommonkotlin.rx.extensions.pairwise
import com.tminus1010.tmcommonkotlin.rx.extensions.toSingle
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.kotlin.subscribeBy
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx3.asObservable
import javax.inject.Inject

class IsReconcileFeatureEnabled @Inject constructor(
    private val app: Application,
    isReconciliationReady: IsReconciliationReady,
) : Observable<Boolean>() {
    private val key = booleanPreferencesKey("IsReconcileFeatureEnabled")

    private fun set(b: Boolean) {
        GlobalScope.launch { app.dataStore.edit { it[key] = b } }
    }

    private val isReconcileFeatureEnabled =
        app.dataStore.data.asObservable()
            .map { it[key] ?: false }
            .distinctUntilChanged()
            .cold()

    init {
        isReconcileFeatureEnabled
            .toSingle()
            .flatMap {
                isReconciliationReady
                    .filter { it }
                    .toSingle()
            }
            .subscribeBy(onSuccess = { set(true) })
    }

    override fun subscribeActual(observer: Observer<in Boolean>) = isReconcileFeatureEnabled.subscribe(observer)

    // TODO("test if this emits when it shouldn't?")
    val onChangeToTrue =
        isReconcileFeatureEnabled
            .pairwise()
            .filter { it.second }
            .map { Unit }!!
}