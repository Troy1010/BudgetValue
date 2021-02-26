package com.tminus1010.budgetvalue.layer_ui

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.dependency_injection.extensions.repo
import com.tminus1010.budgetvalue.model_data.Category
import com.tminus1010.tmcommonkotlin.rx.extensions.launch
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import com.tminus1010.tmcommonkotlin.view.extensions.toast
import com.tminus1010.tmcommonkotlin.view.extensions.v
import kotlinx.android.synthetic.main.frag_new_category.view.*

class NewCategoryFrag : Fragment(R.layout.frag_new_category) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Clicks
        v.btn_done.setOnClickListener {
            try {
                val name = v.edittext_name.text.toString()
                require(name.isNotEmpty())
                val type = v.spinner_type.selectedItem as Category.Type
                Category(name, type)
                    .also { repo.push(it).launch() }
                nav.navigateUp()
            } catch (e: IllegalArgumentException) {
                toast("Invalid name")
            }
        }
        //
        v.spinner_type.adapter = ArrayAdapter(requireContext(), R.layout.text_view, Category.Type.values().drop(1))
    }
}