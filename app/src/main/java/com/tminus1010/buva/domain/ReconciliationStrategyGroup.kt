package com.tminus1010.buva.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class ReconciliationStrategyGroup : Parcelable {
    abstract val resetStrategy: ResetStrategy?
    abstract val planResolutionStrategy: ResolutionStrategy?
    abstract val anytimeResolutionStrategy: ResolutionStrategy?

    @Parcelize
    object Always : ReconciliationStrategyGroup() {
        override val resetStrategy: ResetStrategy?
            get() = null // TODO: Perhaps should be ResetStrategy.MatchPlan?
        override val planResolutionStrategy: ResolutionStrategy
            get() = ResolutionStrategy.MatchPlan
        override val anytimeResolutionStrategy: ResolutionStrategy?
            get() = null
    }

    @Parcelize
    data class Reservoir(
        override val resetStrategy: ResetStrategy? = null,
        override val planResolutionStrategy: ResolutionStrategy? = null,
        override val anytimeResolutionStrategy: ResolutionStrategy? = null,
    ) : ReconciliationStrategyGroup()
}