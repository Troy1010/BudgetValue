package com.tminus1010.budgetvalue._core.shared_features.date_period_getter

import com.tminus1010.budgetvalue._core.data.RepoFacade
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class SettingsUseCasesImpl @Inject constructor(
    private val repoFacade: RepoFacade
): SettingsUseCases {
    override val anchorDateOffset: Observable<Long> =
        repoFacade.anchorDateOffset

    override fun pushAnchorDateOffset(anchorDateOffset: Long?): Completable =
        repoFacade.pushAnchorDateOffset(anchorDateOffset)

    override val blockSize: Observable<Long> =
        repoFacade.blockSize

    override fun pushBlockSize(blockSize: Long?): Completable =
        repoFacade.pushBlockSize(blockSize)
}