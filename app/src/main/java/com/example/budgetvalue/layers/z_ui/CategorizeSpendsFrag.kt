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
import com.example.budgetvalue.databinding.FragCategorizeSpendsBinding
import com.example.budgetvalue.layers.view_models.TransactionVM
import com.example.tmcommonkotlin.vmFactoryFactory

class CategorizeSpendsFrag : Fragment() {
    lateinit var mBinding: FragCategorizeSpendsBinding
    val appComponent by lazy { (requireActivity().application as App).appComponent }
    val transactionVM:TransactionVM by viewModels { vmFactoryFactory { TransactionVM(appComponent.getRepo()) }}
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.frag_categorize_spends, container, false)
        mBinding.lifecycleOwner = this
        mBinding.transactionVM = transactionVM
        return mBinding.root
    }
}