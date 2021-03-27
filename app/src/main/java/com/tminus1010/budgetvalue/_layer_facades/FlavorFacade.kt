package com.tminus1010.budgetvalue._layer_facades

import com.tminus1010.budgetvalue._core.ui.GetExtraMenuItemPartialsUC
import com.tminus1010.budgetvalue._core.ui.LaunchImportUC
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlavorFacade @Inject constructor(
    val getExtraMenuItemPartials: GetExtraMenuItemPartialsUC,
    val launchImport: LaunchImportUC,
)