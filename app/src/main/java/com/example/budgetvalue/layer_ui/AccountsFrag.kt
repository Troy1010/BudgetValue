package com.example.budgetvalue.layer_ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.budgetvalue.App
import com.example.budgetvalue.R
import com.example.budgetvalue.layer_ui.misc.setOnClickListener
import com.tminus1010.tmcommonkotlin.misc.GenericRecyclerViewAdapter
import com.tminus1010.tmcommonkotlin.misc.createVmFactory
import com.trello.rxlifecycle4.android.lifecycle.kotlin.bindToLifecycle
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.frag_accounts.*
import kotlinx.android.synthetic.main.item_account.view.*

class AccountsFrag: Fragment(R.layout.frag_accounts), GenericRecyclerViewAdapter.Callbacks {
    val app by lazy { requireActivity().application as App }
    val accountsVM : AccountsVM by viewModels { createVmFactory { AccountsVM(app.appComponent.getRepo()) } }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupBinds()
        setupObservers()
    }

    private fun setupBinds() {
        btn_add_account.setOnClickListener(accountsVM.intentAddAccount)
    }

    private fun setupViews() {
        recyclerview_accounts.layoutManager = LinearLayoutManager(requireActivity())
        recyclerview_accounts.adapter = GenericRecyclerViewAdapter(requireContext(),this, R.layout.item_account)
    }

    private fun setupObservers() {
        accountsVM.intentAddAccount.mergeWith(accountsVM.intentDeleteAccount.map { Unit } )
            .flatMap { accountsVM.accounts.take(2).skip(1) }
            .bindToLifecycle(viewLifecycleOwner)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                recyclerview_accounts.adapter?.notifyDataSetChanged()
            }
    }

    override fun bindRecyclerItem(holder: GenericRecyclerViewAdapter.ViewHolder, view: View) {
        val account = accountsVM.accounts.value?.get(holder.adapterPosition) ?: return
        view.editText_name?.setText(account.name)
        view.editText_amount?.setText(account.amount.toString())
        view.btn_delete_account.setOnClickListener {
            accountsVM.intentDeleteAccount.onNext(account)
        }
        view.editText_amount.setOnFocusChangeListener { v, b ->
            if (!b) {
                account.amount = view.editText_amount.text.toString().toBigDecimal()
                accountsVM.updateAccount(account)
            }
        }
        view.editText_name.setOnFocusChangeListener { v, b ->
            if (!b) {
                account.name = view.editText_name.text.toString()
                accountsVM.updateAccount(account)
            }
        }
    }

    override fun getRecyclerDataSize(): Int {
        return accountsVM.accounts.value?.size?:0
    }
}