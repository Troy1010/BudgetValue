package com.tminus1010.budgetvalue._core.data

import com.tminus1010.budgetvalue._shared.app_init.data.IAppInitRepo
import com.tminus1010.budgetvalue._shared.date_period_getter.data.ISettingsRepo

interface IMainRepo :
    IAppInitRepo,
    ISettingsRepo
