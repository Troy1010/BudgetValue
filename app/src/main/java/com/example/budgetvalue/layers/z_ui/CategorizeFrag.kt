package com.example.budgetvalue.layers.z_ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.budgetvalue.App
import com.example.budgetvalue.R
import com.example.budgetvalue.databinding.FragCategorizeSpendsBinding
import com.example.budgetvalue.layers.view_models.CategoriesVM
import com.example.budgetvalue.layers.view_models.CategorizeVM
import com.example.budgetvalue.layers.view_models.TransactionsVM
import com.example.tmcommonkotlin.GenericRecyclerViewAdapter
import com.example.tmcommonkotlin.vmFactoryFactory
import kotlinx.android.synthetic.main.frag_categorize_spends.*
import kotlinx.android.synthetic.main.item_category_btn.view.*

class CategorizeFrag : Fragment(), GenericRecyclerViewAdapter.Callbacks {
    lateinit var mBinding: FragCategorizeSpendsBinding
    val appComponent by lazy { (requireActivity().application as App).appComponent }
    val transactionsVM: TransactionsVM by activityViewModels { vmFactoryFactory { TransactionsVM(appComponent.getRepo()) } }
    val categorizeVM:CategorizeVM by viewModels { vmFactoryFactory { CategorizeVM(appComponent.getRepo(), transactionsVM) }}
    val categoriesVM:CategoriesVM by activityViewModels { vmFactoryFactory { CategoriesVM() } }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.frag_categorize_spends, container, false)
        mBinding.lifecycleOwner = this
        mBinding.categorizeSpendsVM = categorizeVM
        mBinding.transactionsVM = transactionsVM
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        // recyclerview
        recyclerview_categories.layoutManager = GridLayoutManager(requireActivity(), 3,
            GridLayoutManager.VERTICAL, true)
        recyclerview_categories.adapter = GenericRecyclerViewAdapter(this, requireActivity(), R.layout.item_category_btn)
    }

    override fun bindRecyclerItemView(view: View, i: Int) {
        view.btn_category.text = categoriesVM.choosableCategories[i].name
        view.btn_category.setOnClickListener {
            categorizeVM.setTransactionCategory(categoriesVM.choosableCategories[i])
        }
    }

    override fun getRecyclerDataSize(): Int {
        return categoriesVM.choosableCategories.size
    }
}