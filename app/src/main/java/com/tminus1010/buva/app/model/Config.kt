package com.tminus1010.buva.app.model

/**
 * Responsible for defining things that might need to be updated after release, like urls or isFeatureEnabled.
 *
 * TODO: Currently, there is no backend to help the definitions, which would allow us to update these values after release.
 */
data class Config(
    val isImageToTextEnabled: Boolean,
)