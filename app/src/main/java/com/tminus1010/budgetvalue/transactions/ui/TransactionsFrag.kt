package com.tminus1010.budgetvalue.transactions.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.extensions.easyText
import com.tminus1010.budgetvalue._core.extensions.getColorByAttr
import com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3.IViewItemRecipe3
import com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3.ViewItemRecipeFactory3
import com.tminus1010.budgetvalue._core.middleware.ui.viewBinding
import com.tminus1010.budgetvalue.databinding.FragTransactionsBinding
import com.tminus1010.budgetvalue.databinding.ItemTextViewBinding
import com.tminus1010.budgetvalue.transactions.TransactionsVM
import com.tminus1010.budgetvalue.transactions.models.Transaction
import com.tminus1010.tmcommonkotlin.core.extensions.toDisplayStr
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.schedulers.Schedulers

@AndroidEntryPoint
class TransactionsFrag : Fragment(R.layout.frag_transactions) {
    private val vb by viewBinding(FragTransactionsBinding::bind)
    private val transactionsVM: TransactionsVM by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        transactionsVM.navToTransation.observe(viewLifecycleOwner) { TransactionFrag.navTo(nav, it) }
        // # TMTableView
        val clickableTextViewRecipeFactory = ViewItemRecipeFactory3<ItemTextViewBinding, Pair<Transaction, String>>(
            { ItemTextViewBinding.inflate(LayoutInflater.from(requireContext())) },
            { (transaction, s), vb, _ ->
                vb.root.setBackgroundColor(
                    requireActivity().theme.getColorByAttr(
                        if (transaction.isCategorized) R.attr.colorBackground else R.attr.colorSecondary
                    )
                )
                vb.root.setOnClickListener { transactionsVM.userTryNavToTransaction(transaction) }
                vb.textview.easyText = s
            }
        )
        transactionsVM.transactions
            .observeOn(Schedulers.computation())
            .map { transactions ->
                transactions.map { transaction ->
                    listOf<IViewItemRecipe3>(
                        clickableTextViewRecipeFactory.createOne(Pair(transaction, transaction.date.toDisplayStr())),
                        clickableTextViewRecipeFactory.createOne(Pair(transaction, transaction.defaultAmount.toString())),
                        clickableTextViewRecipeFactory.createOne(Pair(transaction, transaction.description.take(30)))
                    )
                }
            }
            .observe(viewLifecycleOwner) { recipes2D ->
                vb.tmTableView.initialize(
                    recipeGrid = recipes2D,
                    shouldFitItemWidthsInsideTable = true,
                )
            }
    }
}
