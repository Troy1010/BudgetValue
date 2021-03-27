package com.tminus1010.budgetvalue._core.shared_features.app_init

import com.tminus1010.budgetvalue._core.data.RepoFacade
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

class AppInitBoolUseCasesImpl @Inject constructor(
    private val repoFacade: RepoFacade
): AppInitBoolUseCases {
    override fun fetchAppInitBool(): Boolean =
        repoFacade.fetchAppInitBool()

    override fun pushAppInitBool(boolean: Boolean): Completable =
        repoFacade.pushAppInitBool(boolean)
}