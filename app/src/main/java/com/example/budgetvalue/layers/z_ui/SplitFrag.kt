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
import com.example.budgetvalue.databinding.FragSplitBinding
import com.example.budgetvalue.layers.view_models.SplitVM
import com.example.budgetvalue.layers.z_ui.table_view.MyTableViewAdapter
import com.example.tmcommonkotlin.vmFactoryFactory
import kotlinx.android.synthetic.main.frag_split.*

class SplitFrag: Fragment() {
    val appComponent by lazy { (requireActivity().application as App).appComponent }
    val splitVM : SplitVM by viewModels { vmFactoryFactory { SplitVM(appComponent.getRepo()) } }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mBinding: FragSplitBinding = DataBindingUtil.inflate(inflater, R.layout.frag_split, container, false)
        mBinding.lifecycleOwner = this
        mBinding.splitVM = splitVM
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        content_container.setAdapter(MyTableViewAdapter(requireContext()))
    }
}