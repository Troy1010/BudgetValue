package com.tminus1010.budgetvalue.ui.edit_string

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.databinding.FragEditStringBinding
import com.tminus1010.budgetvalue.framework.view.viewBinding
import com.tminus1010.tmcommonkotlin.coroutines.extensions.observe
import com.tminus1010.tmcommonkotlin.misc.extensions.bind
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditStringFrag : Fragment(R.layout.frag_edit_string) {
    private val vb by viewBinding(FragEditStringBinding::bind)
    private val viewModel: EditStringVM by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Setup
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) { viewModel.userCancel() }
        // # Events
        viewModel.navUp.observe(viewLifecycleOwner) { nav.navigateUp() }
        // # State
        viewModel.editTextVMItem.bind(vb.et)
        vb.buttonsview.bind(viewModel.buttons) { buttons = it }
    }

    companion object {
        fun navTo(nav: NavController, s: String, editStringSharedVM: EditStringSharedVM) {
            editStringSharedVM.initialS = s
            nav.navigate(R.id.editStringFrag)
        }
    }
}