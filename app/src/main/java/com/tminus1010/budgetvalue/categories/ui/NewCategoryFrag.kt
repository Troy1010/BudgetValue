package com.tminus1010.budgetvalue.categories.ui

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.middleware.ui.viewBinding
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.categories.data.ICategoriesRepo
import com.tminus1010.budgetvalue.databinding.FragNewCategoryBinding
import com.tminus1010.tmcommonkotlin.rx.extensions.launch
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import com.tminus1010.tmcommonkotlin.view.extensions.toast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NewCategoryFrag : Fragment(R.layout.frag_new_category) {
    @Inject lateinit var categoriesRepo: ICategoriesRepo // TODO("use VM")
    val vb by viewBinding(FragNewCategoryBinding::bind)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Clicks
        vb.btnDone.setOnClickListener {
            try {
                val name = vb.edittextName.text.toString()
                require(name.isNotEmpty())
                val type = vb.spinnerType.selectedItem as Category.Type
                Category(name, type).also { categoriesRepo.push(it).launch() }
                nav.navigateUp()
            } catch (e: IllegalArgumentException) {
                toast("Invalid name")
            }
        }
        //
        vb.spinnerType.adapter = ArrayAdapter(requireContext(), R.layout.item_text_view, Category.Type.values().drop(1))
    }
}