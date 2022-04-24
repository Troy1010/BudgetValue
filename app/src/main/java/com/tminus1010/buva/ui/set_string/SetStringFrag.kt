package com.tminus1010.buva.ui.set_string

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.tminus1010.buva.R
import com.tminus1010.buva.databinding.FragSetStringBinding
import com.tminus1010.buva.framework.android.viewBinding
import com.tminus1010.tmcommonkotlin.coroutines.extensions.observe
import com.tminus1010.tmcommonkotlin.customviews.extensions.bind
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SetStringFrag : Fragment(R.layout.frag_set_string) {
    private val vb by viewBinding(FragSetStringBinding::bind)
    private val viewModel by viewModels<SetStringVM>()
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
        fun navTo(nav: NavController, s: String, setStringSharedVM: SetStringSharedVM) {
            setStringSharedVM.initialS = s
            nav.navigate(R.id.editStringFrag)
        }
    }
}