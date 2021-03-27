package com.tminus1010.budgetvalue._core.shared_features.app_init

import com.tminus1010.budgetvalue._core.data.Repo
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

class AppInitBoolUseCasesImpl @Inject constructor(
    private val repo: Repo
): AppInitBoolUseCases {
    override fun fetchAppInitBool(): Boolean =
        repo.fetchAppInitBool()

    override fun pushAppInitBool(boolean: Boolean): Completable =
        repo.pushAppInitBool(boolean)
}