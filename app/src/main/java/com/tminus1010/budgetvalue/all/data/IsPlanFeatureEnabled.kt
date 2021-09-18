package com.tminus1010.budgetvalue.all.data

import android.app.Application
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.squareup.moshi.Moshi
import com.tminus1010.budgetvalue._core.extensions.cold
import com.tminus1010.budgetvalue._core.extensions.mapBox
import com.tminus1010.budgetvalue.transactions.domain.TransactionsDomain
import com.tminus1010.tmcommonkotlin.misc.extensions.fromJson
import com.tminus1010.tmcommonkotlin.misc.extensions.toJson
import com.tminus1010.tmcommonkotlin.rx.extensions.pairwise
import com.tminus1010.tmcommonkotlin.rx.extensions.toSingle
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.kotlin.subscribeBy
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx3.asObservable
import java.time.LocalDate
import javax.inject.Inject

class IsPlanFeatureEnabled @Inject constructor(
    private val app: Application,
    transactionsDomain: TransactionsDomain,
    private val moshi: Moshi
) : Observable<Boolean>() {
    private val key = stringPreferencesKey("IsPlanFeatureEnabled")

    private fun set(localDate: LocalDate) {
        GlobalScope.launch { app.dataStore.edit { it[key] = moshi.toJson(localDate) } }
    }

    val dateWhenPlanFeatureWasEnabled =
        app.dataStore.data.asObservable()
            .mapBox { moshi.fromJson<LocalDate>(it[key]) }
            .distinctUntilChanged()
            .cold()

    private val isPlanFeatureEnabled =
        dateWhenPlanFeatureWasEnabled
            .map { (it) -> it != null }
            .distinctUntilChanged()
            .cold()

    init {
        isPlanFeatureEnabled
            .toSingle()
            .flatMap {
                transactionsDomain.spendBlocks
                    .filter { it.size >= 3 && it.takeLast(3).all { it.isFullyCategorized } }
                    .toSingle()
            }
            .subscribeBy(onSuccess = { set(LocalDate.now()) })
    }

    override fun subscribeActual(observer: Observer<in Boolean>) = isPlanFeatureEnabled.subscribe(observer)

    val onChangeToTrue =
        isPlanFeatureEnabled
            .pairwise()
            .filter { it.second }
            .map { Unit }!!
}