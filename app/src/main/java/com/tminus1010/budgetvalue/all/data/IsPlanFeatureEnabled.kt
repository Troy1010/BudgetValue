package com.tminus1010.budgetvalue.all.data

import android.app.Application
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.tminus1010.budgetvalue._core.extensions.cold
import com.tminus1010.budgetvalue.transactions.domain.TransactionsDomain
import com.tminus1010.tmcommonkotlin.rx.extensions.pairwise
import com.tminus1010.tmcommonkotlin.rx.extensions.toSingle
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.kotlin.subscribeBy
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx3.asObservable
import javax.inject.Inject

class IsPlanFeatureEnabled @Inject constructor(
    private val app: Application,
    transactionsDomain: TransactionsDomain,
) : Observable<Boolean>() {
    private val key = booleanPreferencesKey("IsPlanFeatureEnabled")

    private fun set(b: Boolean) {
        GlobalScope.launch { app.dataStore.edit { it[key] = b } }
    }

    private val isPlanFeatureEnabled =
        app.dataStore.data.asObservable()
            .map { it[key] ?: false }
            .distinctUntilChanged()
            .cold()

    init {
        isPlanFeatureEnabled
            .toSingle()
            .flatMap {
                transactionsDomain.transactionBlocks
                    .filter { it.takeLast(3).all { it.isFullyCategorized } }
                    .toSingle()
            }
            .subscribeBy(onSuccess = { set(true) })
    }

    override fun subscribeActual(observer: Observer<in Boolean>) = isPlanFeatureEnabled.subscribe(observer)

    // TODO("test if this emits when it shouldn't?")
    val onChangeToTrue =
        isPlanFeatureEnabled
            .pairwise()
            .filter { it.second }
            .map { Unit }!!
}