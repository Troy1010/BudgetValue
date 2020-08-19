package com.example.budgetvalue.layers.z_ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.budgetvalue.App
import com.example.budgetvalue.R
import com.example.budgetvalue.databinding.FragAccountsBinding
import com.example.budgetvalue.databinding.FragCategorizeSpendsBinding
import com.example.budgetvalue.layers.view_models.AccountsVM
import com.example.tmcommonkotlin.GenericRecyclerViewAdapter
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
        recyclerview_accounts.adapter = GenericRecyclerViewAdapter(this, requireContext(), R.layout.item_account)
    }

    override fun bindRecyclerItemView(view: View, i: Int) {
        view.editText_name?.setText(accountsVM.accounts.value?.get(i)?.name ?: "")
        view.editText_amount?.setText(accountsVM.accounts.value?.get(i)?.amount ?: "")
    }

    override fun getRecyclerDataSize(): Int {
        return accountsVM.accounts.value?.size?:0
    }
}