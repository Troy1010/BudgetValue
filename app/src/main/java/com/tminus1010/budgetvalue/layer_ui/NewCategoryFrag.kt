package com.tminus1010.budgetvalue.layer_ui

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.dependency_injection.IViewModelFactories
import com.tminus1010.budgetvalue.dependency_injection.injection_extensions.appComponent
import com.tminus1010.budgetvalue.dependency_injection.injection_extensions.repo
import com.tminus1010.budgetvalue.model_data.Category
import com.tminus1010.tmcommonkotlin.rx.extensions.launch
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import com.tminus1010.tmcommonkotlin.view.extensions.toast
import kotlinx.android.synthetic.main.frag_new_category.*

class NewCategoryFrag : Fragment(R.layout.frag_new_category), IViewModelFactories {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Clicks
        btn_done.setOnClickListener {
            try {
                val name = edittext_name.text.toString()
                require(name.isNotEmpty())
                val type = spinner_type.selectedItem as Category.Type
                Category(name, type)
                    .also { repo.push(it).launch() }
                nav.navigateUp()
            } catch (e: IllegalArgumentException) {
                toast("Invalid name")
            }
        }
        //
        spinner_type.adapter = ArrayAdapter(requireContext(), R.layout.text_view, Category.Type.values().drop(1))
    }

    override val viewModelFactoriesHelper by lazy { ViewModelFactoriesHelper(requireActivity(), appComponent) }
}