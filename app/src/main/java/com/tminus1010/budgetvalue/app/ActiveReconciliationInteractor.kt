package com.tminus1010.budgetvalue.app

import androidx.annotation.VisibleForTesting
import com.tminus1010.budgetvalue.data.AccountsRepo
import com.tminus1010.budgetvalue.data.ActiveReconciliationRepo
import com.tminus1010.budgetvalue.domain.AccountsAggregate
import com.tminus1010.budgetvalue.domain.Budgeted
import com.tminus1010.budgetvalue.domain.CategoryAmounts
import com.tminus1010.budgetvalue.domain.CategoryAmountsAndTotal
import com.tminus1010.tmcommonkotlin.misc.extensions.sum
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.shareIn
import java.math.BigDecimal
import javax.inject.Inject

class ActiveReconciliationInteractor @Inject constructor(
    accountsRepo: AccountsRepo,
    budgetedInteractor: BudgetedInteractor,
    activeReconciliationRepo: ActiveReconciliationRepo,
) {
    val categoryAmountsAndTotal =
        combine(activeReconciliationRepo.activeReconciliationCAs, accountsRepo.accountsAggregate, budgetedInteractor.budgeted)
        { activeReconciliationCAs, accountsAggregate, budgeted ->
            CategoryAmountsAndTotal.FromTotal(
                categoryAmounts = activeReconciliationCAs,
                total = accountsAggregate.total - budgeted.totalAmount,
            )
        }
            .shareIn(GlobalScope, SharingStarted.Eagerly, 1)

    val defaultAmount = // TODO: Isn't defaultAmount supposed to be calculated by CategoryAmountsAndTotal? Why is there a different calculation here?
        combine(
            activeReconciliationRepo.activeReconciliationCAs,
            accountsRepo.accountsAggregate,
            budgetedInteractor.budgeted,
            ::calcActiveReconciliationDefaultAmount
        )
            .shareIn(GlobalScope, SharingStarted.Eagerly, 1)

    /**
     * For clarification, take a look at the ManualCalculationsForTests excel sheet.
     */
    @VisibleForTesting
    // TODO: Test
    fun calcActiveReconciliationDefaultAmount(activeReconciliationCAs: CategoryAmounts, accountsAggregate: AccountsAggregate, budgeted: Budgeted): BigDecimal {
        return activeReconciliationCAs.defaultAmount(accountsAggregate.total - budgeted.categoryAmounts.values.sum())
    }
}