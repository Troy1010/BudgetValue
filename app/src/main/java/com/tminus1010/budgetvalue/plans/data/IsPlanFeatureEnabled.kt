package com.tminus1010.budgetvalue.plans.data

import android.app.Application
import androidx.annotation.VisibleForTesting
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.squareup.moshi.Moshi
import com.tminus1010.budgetvalue._core.all.extensions.cold
import com.tminus1010.budgetvalue._core.all.extensions.mapBox
import com.tminus1010.budgetvalue.all.data.dataStore
import com.tminus1010.budgetvalue.importZ.data.LatestDateOfMostRecentImport
import com.tminus1010.budgetvalue.transactions.app.interactor.TransactionsInteractor
import com.tminus1010.tmcommonkotlin.misc.extensions.fromJson
import com.tminus1010.tmcommonkotlin.misc.extensions.toJson
import com.tminus1010.tmcommonkotlin.rx.extensions.filterNotNullBox
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
    transactionsInteractor: TransactionsInteractor,
    private val moshi: Moshi,
    latestDateOfMostRecentImport: LatestDateOfMostRecentImport
) : Observable<Boolean>() {
    private val key = stringPreferencesKey("IsPlanFeatureEnabled")

    private fun set(localDate: LocalDate) {
        GlobalScope.launch { app.dataStore.edit { it[key] = moshi.toJson(localDate) } }
    }

    val latestDateOfMostRecentImportWhenPlanFeatureWasEnabled =
        app.dataStore.data.asObservable()
            .mapBox { moshi.fromJson<LocalDate>(it[key]) }
            .distinctUntilChanged()
            .cold()

    private val isPlanFeatureEnabled =
        latestDateOfMostRecentImportWhenPlanFeatureWasEnabled
            .map { (it) -> it != null }
            .distinctUntilChanged()
            .cold()

    init {
        isPlanFeatureEnabled
            .toSingle()
            .flatMap {
                transactionsInteractor.spendBlocks
                    .filter { it.size >= 3 && it.takeLast(3).all { it.isFullyCategorized } }
                    .toSingle()
            }
            .flatMap { latestDateOfMostRecentImport.filterNotNullBox().toSingle() }
            .subscribeBy(onSuccess = { set(it) })
    }

    override fun subscribeActual(observer: Observer<in Boolean>) =
        (isPlanFeatureEnabledOverride ?: isPlanFeatureEnabled)
            .subscribe(observer)

    val onChangeToTrue =
        isPlanFeatureEnabled
            .pairwise()
            .filter { it.second }
            .map { Unit }!!

    companion object {
        @VisibleForTesting
        var isPlanFeatureEnabledOverride: Observable<Boolean>? = null
    }
}