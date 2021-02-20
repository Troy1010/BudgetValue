package com.tminus1010.budgetvalue.layer_ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.extensions.nav
import com.tminus1010.budgetvalue.extensions.v
import kotlinx.android.synthetic.main.frag_category_customization.view.*

class CategoryCustomizationFrag: Fragment(R.layout.frag_category_customization) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        v.btn_cc_done.setOnClickListener { nav.navigateUp() }
    }
}