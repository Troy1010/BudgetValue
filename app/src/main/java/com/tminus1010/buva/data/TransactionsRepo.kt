package com.tminus1010.buva.data

import com.tminus1010.buva.environment.MiscDAO
import javax.inject.Inject

class TransactionsRepo @Inject constructor(
    private val miscDAO: MiscDAO,
) : MiscDAO by miscDAO