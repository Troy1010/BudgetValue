package com.example.budgetvalue.layers.z_ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.budgetvalue.App
import com.example.budgetvalue.R
import com.example.budgetvalue.databinding.FragAccountsBinding
import com.example.budgetvalue.layers.view_models.AccountsVM
import com.example.tmcommonkotlin.GenericRecyclerViewAdapter
import com.example.tmcommonkotlin.logz
import com.example.tmcommonkotlin.vmFactoryFactory
import kotlinx.android.synthetic.main.frag_accounts.*
import kotlinx.android.synthetic.main.item_account.view.*

class AccountsFrag: Fragment(), GenericRecyclerViewAdapter.Callbacks {
    val appComponent by lazy { (requireActivity().application as App).appComponent }
    val accountsVM : AccountsVM by viewModels { vmFactoryFactory { AccountsVM(appComponent.getRepo()) } }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mBinding: FragAccountsBinding = DataBindingUtil.inflate(inflater, R.layout.frag_accounts, container, false)
        mBinding.lifecycleOwner = this
        mBinding.accountsVM = accountsVM
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        recyclerview_accounts.layoutManager = LinearLayoutManager(requireActivity())
        recyclerview_accounts.adapter = GenericRecyclerViewAdapter(this, requireContext(), R.layout.item_account)
        btn_do_something.setOnClickListener {

            logz("accountsVM.accounts.value?.size?:${accountsVM.accounts.value?.size?:0}")
            recyclerview_accounts.adapter?.notifyDataSetChanged()
        }
    }

    var bNotifyDataSetChanged = true
    private fun setupObservers() {
        accountsVM.accounts.observe(viewLifecycleOwner) {
            if (bNotifyDataSetChanged) {
                bNotifyDataSetChanged = false
                recyclerview_accounts.adapter?.notifyDataSetChanged()
            }
        }
    }

    override fun bindRecyclerItemView(view: View, i: Int) {
        val account = accountsVM.accounts.value?.get(i) ?: return
        view.editText_name?.setText(account.name)
        view.editText_amount?.setText(account.amount)
        view.btn_delete_account.setOnClickListener {
            bNotifyDataSetChanged = true
            accountsVM.intentDeleteAccount.onNext(account)
        }
        view.editText_amount.setOnFocusChangeListener { v, b ->
            if (!b) {
                account.amount = view.editText_amount.text.toString()
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