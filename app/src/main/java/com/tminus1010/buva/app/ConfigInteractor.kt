package com.tminus1010.buva.app

import com.tminus1010.buva.app.model.Config
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class ConfigInteractor @Inject constructor() {
    val config =
        flowOf(
            Config(
                isImageToTextEnabled = false,
            )
        )
}