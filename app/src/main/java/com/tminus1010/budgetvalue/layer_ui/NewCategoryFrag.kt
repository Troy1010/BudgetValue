package com.tminus1010.budgetvalue.layer_ui

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.databinding.FragNewCategoryBinding
import com.tminus1010.budgetvalue.dependency_injection.ViewModelProviders
import com.tminus1010.budgetvalue.dependency_injection.injection_extensions.appComponent
import com.tminus1010.budgetvalue.dependency_injection.injection_extensions.domain
import com.tminus1010.budgetvalue.middleware.ui.viewBinding
import com.tminus1010.budgetvalue.features.categories.Category
import com.tminus1010.tmcommonkotlin.rx.extensions.launch
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import com.tminus1010.tmcommonkotlin.view.extensions.toast

class NewCategoryFrag : Fragment(R.layout.frag_new_category), IViewModels {
    val vb by viewBinding(FragNewCategoryBinding::bind)
    override val viewModelProviders by lazy { ViewModelProviders(requireActivity(), appComponent) }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Clicks
        vb.btnDone.setOnClickListener {
            try {
                val name = vb.edittextName.text.toString()
                require(name.isNotEmpty())
                val type = vb.spinnerType.selectedItem as Category.Type
                Category(name, type).also { domain.push(it).launch() }
                nav.navigateUp()
            } catch (e: IllegalArgumentException) {
                toast("Invalid name")
            }
        }
        //
        vb.spinnerType.adapter = ArrayAdapter(requireContext(), R.layout.text_view, Category.Type.values().drop(1))
    }
}