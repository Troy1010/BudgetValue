package com.tminus1010.buva.app

import android.app.Application
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

@Deprecated("This is not maintained due to lack of effort")
class IsPlanFeatureEnabled @Inject constructor(
    private val app: Application,
    transactionsInteractor: TransactionsInteractor,
) {
    val flow = flowOf(true)
    // TODO()
//    private val key = stringPreferencesKey("IsPlanFeatureEnabled")
//
//    private fun set(localDate: LocalDate) {
//        GlobalScope.launch { app.dataStore.edit { it[key] = moshi.toJson(localDate) } }
//    }
//
//    val latestDateOfMostRecentImportWhenPlanFeatureWasEnabled =
//        app.dataStore.data.asObservable()
//            .mapBox { moshi.fromJson<LocalDate>(it[key]) }
//            .distinctUntilChanged()
//            .cold()
//
//    private val isPlanFeatureEnabled =
////        latestDateOfMostRecentImportWhenPlanFeatureWasEnabled
////            .map { (it) -> it != null }
////            .distinctUntilChanged()
//        // * Requirement is not obvious. Perhaps PlanFeature should be enabled by default?
//        Observable.just(true)
//            .cold()
//
//    init {
//        isPlanFeatureEnabled
//            .toSingle()
//            .flatMap {
//                transactionsInteractor.spendBlocks.asObservable()
//                    .filter { it.size >= 3 && it.takeLast(3).all { it.isFullyCategorized } }
//                    .toSingle()
//            }
//            .flatMap { latestDateOfMostRecentImportRepo.filterNotNullBox().toSingle() }
//            .subscribeBy(onSuccess = { set(it) })
//    }
//
//    val onChangeToTrue =
//        isPlanFeatureEnabled
//            .pairwise()
//            .filter { it.second }
//            .map { Unit }
//            .asFlow()
}