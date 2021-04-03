package com.tminus1010.budgetvalue._core.data

import com.tminus1010.budgetvalue._core.data.ISharedPrefWrapper
import com.tminus1010.budgetvalue._core.data.MiscDAO
import com.tminus1010.budgetvalue._core.data.SharedPrefWrapper
import com.tminus1010.budgetvalue._core.data.UserCategoriesDAO
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A Repo is the facade to the data layer.
 * If you ever change how the data is written/retrieved, all other layers will not require updates.
 */
@Singleton
class RepoFacade @Inject constructor(
    private val sharedPrefWrapper: SharedPrefWrapper,
    private val miscDAO: MiscDAO,
    private val userCategoriesDAO: UserCategoriesDAO,
) : ISharedPrefWrapper by sharedPrefWrapper,
    MiscDAO by miscDAO,
    UserCategoriesDAO by userCategoriesDAO