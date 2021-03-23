package com.tminus1010.budgetvalue.layer_ui

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding4.view.clicks
import com.tminus1010.budgetvalue.middleware.ui.GenViewHolder2
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.databinding.FragImportBinding
import com.tminus1010.budgetvalue.databinding.ItemAccountBinding
import com.tminus1010.budgetvalue.dependency_injection.ViewModelProviders
import com.tminus1010.budgetvalue.dependency_injection.injection_extensions.appComponent
import com.tminus1010.budgetvalue.dependency_injection.injection_extensions.flavorIntersection
import com.tminus1010.budgetvalue.middleware.ui.bindOutgoing
import com.tminus1010.budgetvalue.middleware.ui.viewBinding
import com.tminus1010.budgetvalue.features_shared.IViewModels
import com.tminus1010.budgetvalue.middleware.toMoneyBigDecimal
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.rx.extensions.pairwise
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers

class ImportFrag : Fragment(R.layout.frag_import), IViewModels {
    val binding by viewBinding(FragImportBinding::bind)
    override val viewModelProviders by lazy { ViewModelProviders(requireActivity(), appComponent) }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Clicks
        binding.btnImport.clicks().subscribe { flavorIntersection.launchImport(requireActivity() as HostActivity) }
        binding.btnAddAccount.clicks().subscribe(accountsVM.intentAddAccount)
        // # RecyclerView
        accountsVM.accounts
            .pairwise()
            .filter { it.first.size != it.second.size }
            .observeOn(AndroidSchedulers.mainThread())
            .observe(viewLifecycleOwner) { binding.recyclerviewAccounts.adapter?.notifyDataSetChanged() }
        binding.recyclerviewAccounts.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = object : RecyclerView.Adapter<GenViewHolder2<ItemAccountBinding>>() {
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                    ItemAccountBinding.inflate(layoutInflater, parent, false)
                        .let { GenViewHolder2(it) }
                override fun getItemCount() = accountsVM.accounts.value.size
                override fun onBindViewHolder(holder: GenViewHolder2<ItemAccountBinding>, position: Int) {
                    val account = accountsVM.accounts.value[holder.adapterPosition]
                    holder.binding.btnDeleteAccount.clicks()
                        .map { account }
                        .subscribe(accountsVM.intentDeleteAccount)
                    holder.binding.editTextName.apply {
                        setText(account.name)
                        bindOutgoing(accountsVM.intentUpdateAccount,
                            { account.copy(name = it) })
                    }
                    holder.binding.editTextAmount.apply {
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