package com.tminus1010.budgetvalue.plans.app.convenience_service

import android.app.Application
import androidx.annotation.VisibleForTesting
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.tminus1010.budgetvalue.all_features.all_layers.extensions.cold
import com.tminus1010.budgetvalue.all_features.all_layers.extensions.mapBox
import com.tminus1010.budgetvalue.all_features.data.MoshiProvider.moshi
import com.tminus1010.budgetvalue.all_features.data.dataStore
import com.tminus1010.budgetvalue.all_features.data.repo.LatestDateOfMostRecentImportRepo
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

class IsPlanFeatureEnabledUC @Inject constructor(
    private val app: Application,
    transactionsInteractor: TransactionsInteractor,
    latestDateOfMostRecentImportRepo: LatestDateOfMostRecentImportRepo,
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
//        latestDateOfMostRecentImportWhenPlanFeatureWasEnabled
//            .map { (it) -> it != null }
//            .distinctUntilChanged()
        // * Requirement is not obvious. Perhaps PlanFeature should be enabled by default?
        Observable.just(true)
            .cold()

    init {
        isPlanFeatureEnabled
            .toSingle()
            .flatMap {
                transactionsInteractor.spendBlocks
                    .filter { it.size >= 3 && it.takeLast(3).all { it.isFullyCategorized } }
                    .toSingle()
            }
            .flatMap { latestDateOfMostRecentImportRepo.filterNotNullBox().toSingle() }
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