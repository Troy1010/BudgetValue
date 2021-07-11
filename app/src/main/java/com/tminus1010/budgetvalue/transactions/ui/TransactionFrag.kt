package com.tminus1010.budgetvalue.transactions.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.extensions.easyText
import com.tminus1010.budgetvalue._core.extensions.getColorByAttr
import com.tminus1010.budgetvalue._core.middleware.ui.ButtonItem
import com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3.ViewItemRecipeFactory3
import com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3.recipeFactories
import com.tminus1010.budgetvalue._core.middleware.ui.viewBinding
import com.tminus1010.budgetvalue.databinding.FragTransactionBinding
import com.tminus1010.budgetvalue.databinding.ItemTextViewBinding
import com.tminus1010.budgetvalue.transactions.TransactionVM
import com.tminus1010.budgetvalue.transactions.models.Transaction
import com.tminus1010.tmcommonkotlin.core.extensions.toDisplayStr
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import com.tminus1010.tmcommonkotlin.view.extensions.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TransactionFrag : Fragment(R.layout.frag_transaction) {
    private val vb by viewBinding(FragTransactionBinding::bind)
    private val transactionVM: TransactionVM by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Mediation
        _transaction?.also { _transaction = null; transactionVM.setup(it) }
        //
        transactionVM.navUp.observe(viewLifecycleOwner) { nav.navigateUp() }
        vb.buttonsview.buttons = listOfNotNull(
            ButtonItem(
                title = "Clear",
                onClick = { transactionVM.userClearTransaction() }
            )
        ).reversed()
        // # TMTableView_Title
        val clickableTextViewRecipeFactory = ViewItemRecipeFactory3<ItemTextViewBinding, Pair<Transaction, String>>(
            { ItemTextViewBinding.inflate(LayoutInflater.from(requireContext())) },
            { (transaction, s), vb, _ ->
                vb.root.setBackgroundColor(
                    requireActivity().theme.getColorByAttr(
                        if (transaction.isCategorized) R.attr.colorBackground else R.attr.colorSecondary
                    )
                )
                vb.textview.easyText = s
            }
        )
        vb.tmTableViewTitle.initialize(
            recipeGrid = listOf(
                listOf(
                    clickableTextViewRecipeFactory.createOne(Pair(transactionVM.transaction, transactionVM.transaction.date.toDisplayStr())),
                    clickableTextViewRecipeFactory.createOne(Pair(transactionVM.transaction, transactionVM.transaction.defaultAmount.toString())),
                    clickableTextViewRecipeFactory.createOne(Pair(transactionVM.transaction, transactionVM.transaction.description.take(30)))
                )
            ),
            shouldFitItemWidthsInsideTable = true,
        )
        // # TMTableView
        if (transactionVM.transaction.categoryAmounts.isEmpty())
            toast("This transaction is empty")
        else
            vb.tmTableView.initialize(
                recipeGrid = listOf(
                    listOf(
                        recipeFactories.textView.createOne("Default"),
                        recipeFactories.textView.createOne(transactionVM.transaction.defaultAmount),
                    ),
                    *transactionVM.transaction.categoryAmounts.map {
                        listOf(
                            recipeFactories.textView.createOne(it.key.name),
                            recipeFactories.textView.createOne(it.value),
                        )
                    }.toTypedArray()
                ),
                shouldFitItemWidthsInsideTable = true,
            )
    }

    companion object {
        private var _transaction: Transaction? = null
        fun navTo(nav: NavController, transaction: Transaction) {
            _transaction = transaction
            nav.navigate(R.id.transactionFrag)
        }
    }
}