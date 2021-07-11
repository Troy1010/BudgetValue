package com.tminus1010.budgetvalue.transactions.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3.recipeFactories
import com.tminus1010.budgetvalue._core.middleware.ui.viewBinding
import com.tminus1010.budgetvalue.databinding.FragTransactionBinding
import com.tminus1010.budgetvalue.transactions.TransactionVM
import com.tminus1010.budgetvalue.transactions.models.Transaction
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TransactionFrag : Fragment(R.layout.frag_transaction) {
    private val vb by viewBinding(FragTransactionBinding::bind)
    private val transactionVM: TransactionVM by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Mediation
        transactionVM.setup(transaction!!.also { transaction = null })
        // # TMTableView
        vb.tmTableView.initialize(
            recipeGrid = transactionVM.transaction.categoryAmounts.map {
                listOf(
                    recipeFactories.textView.createOne(it.key.name),
                    recipeFactories.textView.createOne(it.value),
                )
            },
            shouldFitItemWidthsInsideTable = true,
        )
    }

    companion object {
        var transaction: Transaction? = null
        fun navTo(nav: NavController, _transaction: Transaction) {
            transaction = _transaction
            nav.navigate(R.id.transactionFrag)
        }
    }
}