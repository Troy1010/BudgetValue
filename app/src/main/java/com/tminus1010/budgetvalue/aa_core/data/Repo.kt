package com.tminus1010.budgetvalue.aa_core.data

import javax.inject.Inject
import javax.inject.Singleton

/**
 * A Repo is the facade to the data layer.
 * If you ever change how the data is written/retrieved, all other layers will not require updates.
 */
@Singleton
class Repo @Inject constructor(
    private val sharedPrefWrapper: SharedPrefWrapper,
    private val miscDAO: MiscDAO,
    private val userCategoriesDAO: UserCategoriesDAO,
) : ISharedPrefWrapper by sharedPrefWrapper,
    MiscDAO by miscDAO,
    UserCategoriesDAO by userCategoriesDAO