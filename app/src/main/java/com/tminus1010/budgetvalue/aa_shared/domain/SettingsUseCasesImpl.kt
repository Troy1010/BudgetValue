package com.tminus1010.budgetvalue.aa_shared.domain

import com.tminus1010.budgetvalue.aa_core.data.Repo
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class SettingsUseCasesImpl @Inject constructor(
    private val repo: Repo
): SettingsUseCases {
    override val anchorDateOffset: Observable<Long> =
        repo.anchorDateOffset

    override fun pushAnchorDateOffset(anchorDateOffset: Long?): Completable =
        repo.pushAnchorDateOffset(anchorDateOffset)

    override val blockSize: Observable<Long> =
        repo.blockSize

    override fun pushBlockSize(blockSize: Long?): Completable =
        repo.pushBlockSize(blockSize)
}