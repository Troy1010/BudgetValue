package com.tminus1010.budgetvalue.layer_ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding4.view.clicks
import com.tminus1010.budgetvalue.GenViewHolder
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.dependency_injection.ViewModelProviders
import com.tminus1010.budgetvalue.dependency_injection.injection_extensions.appComponent
import com.tminus1010.budgetvalue.dependency_injection.injection_extensions.flavorIntersection
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.rx.extensions.pairwise
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import kotlinx.android.synthetic.main.frag_import.*
import kotlinx.android.synthetic.main.item_account.view.*

class ImportFrag : Fragment(R.layout.frag_import), IViewModels {
    override val viewModelProviders by lazy { ViewModelProviders(requireActivity(), appComponent) }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Clicks
        btn_import.clicks().subscribe { flavorIntersection.launchImport(requireActivity()) }
        btn_add_account.clicks().subscribe(accountsVM.intentAddAccount)
        // # RecyclerView
        accountsVM.accounts
            .pairwise()
            .filter { it.first.size != it.second.size }
            .observeOn(AndroidSchedulers.mainThread())
            .observe(viewLifecycleOwner) { recyclerview_accounts.adapter?.notifyDataSetChanged() }
        recyclerview_accounts.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = object : RecyclerView.Adapter<GenViewHolder>() {
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                    LayoutInflater.from(requireContext()).inflate(R.layout.item_account, parent, false)
                        .let { GenViewHolder(it) }
                override fun getItemCount() = accountsVM.accounts.value?.size ?: 0
                override fun onBindViewHolder(holder: GenViewHolder, position: Int) {
                    val account = accountsVM.accounts.value?.get(holder.adapterPosition)!!
                    holder.itemView.btn_delete_account.clicks()
                        .map { account }
                        .subscribe(accountsVM.intentDeleteAccount)
                    holder.itemView.editText_name.apply {
                        setText(account.name)
                        setOnFocusChangeListener { _, b ->
                            if (!b)
                                account.copy(name = holder.itemView.editText_name.text.toString())
                                    .also { accountsVM.intentUpdateAmmount.onNext(it) }
                        }
                    }
                    holder.itemView.editText_amount.apply {
                        setText(account.amount.toString())
                        setOnFocusChangeListener { _, b ->
                            if (!b)
                                account.copy(amount = holder.itemView.editText_amount.text.toString().toBigDecimal())
                                    .also { accountsVM.intentUpdateAmmount.onNext(it) }
                        }
                    }
                }
            }
        }
    }
}