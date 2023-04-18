package com.tminus1010.buva.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class ReconciliationStrategyGroup : Parcelable {
    abstract val resetStrategy: ResetStrategy?
    abstract val planResolutionStrategy: ResolutionStrategy
    abstract val anytimeResolutionStrategy: ResolutionStrategy

    @Parcelize
    object Always : ReconciliationStrategyGroup() {
        override val resetStrategy: ResetStrategy
            get() = ResetStrategy.Basic(null)
        override val planResolutionStrategy: ResolutionStrategy
            get() = ResolutionStrategy.MatchPlan
        override val anytimeResolutionStrategy: ResolutionStrategy
            get() = ResolutionStrategy.Basic()
    }

    @Parcelize
    object Unlimited : ReconciliationStrategyGroup() {
        override val resetStrategy: ResetStrategy
            get() = ResetStrategy.Basic(null)
        override val planResolutionStrategy: ResolutionStrategy
            get() = ResolutionStrategy.Basic()
        override val anytimeResolutionStrategy: ResolutionStrategy
            get() = ResolutionStrategy.Basic()
    }

    @Parcelize
    data class Reservoir(
        override val resetStrategy: ResetStrategy? = null,
    ) : ReconciliationStrategyGroup() {
        override val planResolutionStrategy: ResolutionStrategy
            get() = ResolutionStrategy.Basic()
        override val anytimeResolutionStrategy: ResolutionStrategy
            get() = ResolutionStrategy.Basic()
    }
}