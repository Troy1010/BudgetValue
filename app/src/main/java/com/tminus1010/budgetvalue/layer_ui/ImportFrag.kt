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
import com.tminus1010.budgetvalue.extensions_intersecting.accountsVM
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.frag_import.*
import kotlinx.android.synthetic.main.item_account.view.*

class ImportFrag : Fragment(R.layout.frag_import) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Clicks
        btn_import.clicks().subscribe { launchImport(this.requireActivity()) }
        btn_add_account.clicks().subscribe(accountsVM.intentAddAccount)
        // # RecyclerView
        accountsVM.intentAddAccount.mergeWith(accountsVM.intentDeleteAccount.map { Unit })
            .flatMap { accountsVM.accounts.take(2).skip(1) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(AndroidSchedulers.mainThread())
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
                            if (!b) {
                                account.name = holder.itemView.editText_name.text.toString()
                                accountsVM.updateAccount(account)
                            }
                        }
                    }
                    holder.itemView.editText_amount.apply {
                        setText(account.amount.toString())
                        setOnFocusChangeListener { _, b ->
                            if (!b) {
                                account.amount = holder.itemView.editText_amount.text.toString().toBigDecimal()
                                accountsVM.updateAccount(account)
                            }
                        }
                    }
                }
            }
        }
    }
}