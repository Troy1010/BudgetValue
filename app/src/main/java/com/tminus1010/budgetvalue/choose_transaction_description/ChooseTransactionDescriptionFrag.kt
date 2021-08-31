package com.tminus1010.budgetvalue.choose_transaction_description

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.extensions.viewItemRecipe
import com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3.IViewItemRecipe3
import com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3.bindItemTextViewBinding
import com.tminus1010.budgetvalue._core.middleware.ui.viewBinding
import com.tminus1010.budgetvalue.databinding.FragTransactionsBinding
import com.tminus1010.budgetvalue.transactions.TransactionsVM
import com.tminus1010.tmcommonkotlin.core.extensions.toDisplayStr
import com.tminus1010.tmcommonkotlin.rx.extensions.value
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChooseTransactionDescriptionFrag : Fragment(R.layout.frag_transactions) {
    private val vb by viewBinding(FragTransactionsBinding::bind)
    private val transactionsVM by activityViewModels<TransactionsVM>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb.tmTableView.initialize(
            recipeGrid = (transactionsVM.transactions.value ?: emptyList())
                .distinctBy { it.description }
                .map { transaction ->
                    listOf<IViewItemRecipe3>(
                        viewItemRecipe(bindItemTextViewBinding, transaction.date.toDisplayStr()),
                        viewItemRecipe(bindItemTextViewBinding, transaction.amount.toString()),
                        viewItemRecipe(bindItemTextViewBinding, transaction.description.take(30)),
                    )
                },
            shouldFitItemWidthsInsideTable = true,
        )

//        val clickableTextViewRecipeFactory = ViewItemRecipeFactory3<ItemTextViewBinding, Pair<Transaction, String>>(
//            { ItemTextViewBinding.inflate(LayoutInflater.from(requireContext())) },
//            { (transaction, s), vb, _ ->
//                vb.root.setBackgroundColor(
//                    requireActivity().theme.getColorByAttr(
//                        if (transaction.isCategorized) R.attr.colorBackground else R.attr.colorSecondary
//                    )
//                )
//                vb.root.setOnClickListener { transactionsVM.userTryNavToTransaction(transaction) }
//                vb.textview.easyText = s
//            }
//        )
//        transactionsVM.transactions
//            .observeOn(Schedulers.computation())
//            .map { transactions ->
//                transactions.map { transaction ->
//                    listOf<IViewItemRecipe3>(
//                        clickableTextViewRecipeFactory.createOne(Pair(transaction, transaction.date.toDisplayStr())),
//                        clickableTextViewRecipeFactory.createOne(Pair(transaction, transaction.amount.toString())),
//                        clickableTextViewRecipeFactory.createOne(Pair(transaction, transaction.description.take(30))),
//                    )
//                }
//            }
//            .observe(viewLifecycleOwner) { recipes2D ->
//                vb.tmTableView.initialize(
//                    recipeGrid = recipes2D,
//                    shouldFitItemWidthsInsideTable = true,
//                )
//            }
    }

    companion object {
        fun navTo(nav: NavController) {
            nav.navigate(R.id.chooseTransactionDescriptionFrag)
        }
    }
}