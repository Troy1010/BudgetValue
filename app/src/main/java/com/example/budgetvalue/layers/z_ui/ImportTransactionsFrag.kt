package com.example.budgetvalue.layers.z_ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.budgetvalue.App
import com.example.budgetvalue.R
import com.example.budgetvalue.databinding.FragImportTransactionsBinding
import com.example.budgetvalue.layers.view_models.TransactionsVM
import com.example.tmcommonkotlin.vmFactoryFactory

class ImportTransactionsFrag: Fragment() {
    val appComponent by lazy { (requireActivity().application as App).appComponent }
    val transactionsVM: TransactionsVM by activityViewModels { vmFactoryFactory { TransactionsVM(appComponent.getRepo()) } }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mBinding: FragImportTransactionsBinding = DataBindingUtil.inflate(inflater, R.layout.frag_import_transactions, container, false)
        mBinding.lifecycleOwner = this
        mBinding.transactionsVM = transactionsVM
        return mBinding.root
    }
}