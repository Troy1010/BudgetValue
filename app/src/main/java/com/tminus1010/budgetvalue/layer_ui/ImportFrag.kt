package com.tminus1010.budgetvalue.layer_ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.rxbinding4.view.clicks
import com.tminus1010.budgetvalue.App
import com.tminus1010.budgetvalue.GenericRecyclerViewAdapter6
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.extensions.viewModels2
import com.tminus1010.tmcommonkotlin_rx.observe
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.frag_import.*
import kotlinx.android.synthetic.main.item_account.view.*

class ImportFrag : Fragment(R.layout.frag_import) {
    val app by lazy { requireActivity().application as App }
    val accountsVM: AccountsVM by viewModels2 { AccountsVM(app.appComponent.getRepo()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupBinds()
    }

    private fun setupBinds() {
        btn_add_account.clicks().subscribe(accountsVM.intentAddAccount)
        accountsVM.intentAddAccount.mergeWith(accountsVM.intentDeleteAccount.map { Unit })
            .flatMap { accountsVM.accounts.take(2).skip(1) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(AndroidSchedulers.mainThread())
            .observe(viewLifecycleOwner) { recyclerview_accounts.adapter?.notifyDataSetChanged() }
    }

    private fun setupViews() {
        recyclerview_accounts.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = GenericRecyclerViewAdapter6(requireContext(), rvAdapterParams)
        }
    }

    val rvAdapterParams = object : GenericRecyclerViewAdapter6.Params {
        override val itemLayout = R.layout.item_account
        override val size = accountsVM.accounts.value?.size ?: 0
        override fun bindRecyclerItem(holder: GenericRecyclerViewAdapter6.ViewHolder, view: View) {
            val account = accountsVM.accounts.value?.get(holder.adapterPosition)!!
            view.btn_delete_account.clicks()
                .map { account }
                .subscribe(accountsVM.intentDeleteAccount)
            view.editText_name.apply {
                setText(account.name)
                setOnFocusChangeListener { _, b ->
                    if (!b) {
                        account.name = view.editText_name.text.toString()
                        accountsVM.updateAccount(account)
                    }
                }
            }
            view.editText_amount.apply {
                setText(account.amount.toString())
                setOnFocusChangeListener { _, b ->
                    if (!b) {
                        account.amount = view.editText_amount.text.toString().toBigDecimal()
                        accountsVM.updateAccount(account)
                    }
                }
            }
        }
    }
}