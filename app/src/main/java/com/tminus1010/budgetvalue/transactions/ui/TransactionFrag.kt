package com.tminus1010.budgetvalue.transactions.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.thekhaeng.recyclerviewmargin.LayoutMarginDecoration
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.extensions.easyText
import com.tminus1010.budgetvalue._core.extensions.getColorByAttr
import com.tminus1010.budgetvalue._core.middleware.ui.ButtonRVItem
import com.tminus1010.budgetvalue._core.middleware.ui.GenViewHolder2
import com.tminus1010.budgetvalue._core.middleware.ui.LifecycleRVAdapter
import com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3.ViewItemRecipeFactory3
import com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3.recipeFactories
import com.tminus1010.budgetvalue._core.middleware.ui.viewBinding
import com.tminus1010.budgetvalue._core.ui.data_binding.bindButtonRVItem
import com.tminus1010.budgetvalue.databinding.FragTransactionBinding
import com.tminus1010.budgetvalue.databinding.ItemButtonBinding
import com.tminus1010.budgetvalue.databinding.ItemTextViewBinding
import com.tminus1010.budgetvalue.transactions.TransactionVM
import com.tminus1010.budgetvalue.transactions.models.Transaction
import com.tminus1010.tmcommonkotlin.core.extensions.toDisplayStr
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import com.tminus1010.tmcommonkotlin.view.extensions.toPX
import com.tminus1010.tmcommonkotlin.view.extensions.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TransactionFrag : Fragment(R.layout.frag_transaction) {
    private val vb by viewBinding(FragTransactionBinding::bind)
    private val transactionVM: TransactionVM by viewModels()
    val transaction by lazy { transactionVM.transaction }
    var btns = emptyList<ButtonRVItem>()
        set(value) {
            field = value; vb.recyclerviewButtons.adapter?.notifyDataSetChanged()
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Mediation
        transactionVM.setup(_transaction!!.also { _transaction = null })
        //
        transactionVM.navUp.observe(viewLifecycleOwner) { nav.navigateUp() }
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
                    clickableTextViewRecipeFactory.createOne(Pair(transaction, transaction.date.toDisplayStr())),
                    clickableTextViewRecipeFactory.createOne(Pair(transaction, transaction.defaultAmount.toString())),
                    clickableTextViewRecipeFactory.createOne(Pair(transaction, transaction.description.take(30)))
                )
            ),
            shouldFitItemWidthsInsideTable = true,
        )
        // # TMTableView
        if (transactionVM.transaction.categoryAmounts.isEmpty())
            toast("This transaction is empty")
        else
            vb.tmTableView.initialize(
                recipeGrid = transactionVM.transaction.categoryAmounts.map {
                    listOf(
                        recipeFactories.textView.createOne(it.key.name),
                        recipeFactories.textView.createOne(it.value),
                    )
                },
                shouldFitItemWidthsInsideTable = true,
            )
        // # Button RecyclerView
        vb.recyclerviewButtons.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true)
        vb.recyclerviewButtons.addItemDecoration(LayoutMarginDecoration(8.toPX(requireContext())))
        vb.recyclerviewButtons.adapter = object : LifecycleRVAdapter<GenViewHolder2<ItemButtonBinding>>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                GenViewHolder2(ItemButtonBinding.inflate(LayoutInflater.from(requireContext()), parent, false))

            override fun onViewAttachedToWindow(holder: GenViewHolder2<ItemButtonBinding>, lifecycle: LifecycleOwner) {
                holder.vb.btnItem.bindButtonRVItem(lifecycle, btns[holder.adapterPosition])
            }

            override fun getItemCount() = btns.size
        }
        btns = listOfNotNull(
            ButtonRVItem(
                title = "Clear",
                onClick = { transactionVM.userClearTransaction() }
            )
        ).reversed()
    }

    companion object {
        private var _transaction: Transaction? = null
        fun navTo(nav: NavController, transaction: Transaction) {
            _transaction = transaction
            nav.navigate(R.id.transactionFrag)
        }
    }
}