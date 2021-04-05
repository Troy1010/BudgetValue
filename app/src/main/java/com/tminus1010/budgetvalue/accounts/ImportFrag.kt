package com.tminus1010.budgetvalue.accounts

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding4.view.clicks
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.LaunchImportUC
import com.tminus1010.budgetvalue._core.middleware.toMoneyBigDecimal
import com.tminus1010.budgetvalue._core.middleware.ui.GenViewHolder2
import com.tminus1010.budgetvalue._core.middleware.ui.bindOutgoing
import com.tminus1010.budgetvalue._core.middleware.ui.viewBinding
import com.tminus1010.budgetvalue._core.ui.HostActivity
import com.tminus1010.budgetvalue.databinding.FragImportBinding
import com.tminus1010.budgetvalue.databinding.ItemAccountBinding
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.rx.extensions.pairwise
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import javax.inject.Inject

@AndroidEntryPoint
class ImportFrag : Fragment(R.layout.frag_import) {
    @Inject lateinit var launchImportUC: LaunchImportUC
    val accountsVM: AccountsVM by activityViewModels()
    val vb by viewBinding(FragImportBinding::bind)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Clicks
        vb.btnImport.clicks().subscribe { launchImportUC(requireActivity() as HostActivity) }
        vb.btnAddAccount.clicks().subscribe(accountsVM.intentAddAccount)
        // # RecyclerView
        accountsVM.accounts
            .pairwise()
            .filter { it.first.size != it.second.size }
            .observeOn(AndroidSchedulers.mainThread())
            .observe(viewLifecycleOwner) { vb.recyclerviewAccounts.adapter?.notifyDataSetChanged() }
        vb.recyclerviewAccounts.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = object : RecyclerView.Adapter<GenViewHolder2<ItemAccountBinding>>() {
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                    ItemAccountBinding.inflate(layoutInflater, parent, false)
                        .let { GenViewHolder2(it) }
                override fun getItemCount() = accountsVM.accounts.value.size
                override fun onBindViewHolder(holder: GenViewHolder2<ItemAccountBinding>, position: Int) {
                    val account = accountsVM.accounts.value[holder.adapterPosition]
                    holder.vb.btnDeleteAccount.clicks()
                        .map { account }
                        .subscribe(accountsVM.intentDeleteAccount)
                    holder.vb.editTextName.apply {
                        setText(account.name)
                        bindOutgoing(accountsVM.intentUpdateAccount,
                            { account.copy(name = it) })
                    }
                    holder.vb.editTextAmount.apply {
                        setText(account.amount.toString())
                        bindOutgoing(accountsVM.intentUpdateAccount,
                            toT = { account.copy(amount = it.toMoneyBigDecimal()) },
                            toDisplayable = { it.amount })
                    }
                }
            }
        }
    }
}