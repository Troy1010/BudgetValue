package com.tminus1010.buva.ui.set_string

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.tminus1010.buva.R
import com.tminus1010.buva.all_layers.KEY1
import com.tminus1010.buva.all_layers.KEY2
import com.tminus1010.buva.all_layers.android.viewBinding
import com.tminus1010.buva.databinding.FragSetStringBinding
import com.tminus1010.buva.environment.ParcelableLambdaWrapper
import com.tminus1010.tmcommonkotlin.customviews.extensions.bind
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SetStringFrag : Fragment(R.layout.frag_set_string) {
    private val vb by viewBinding(FragSetStringBinding::bind)
    private val viewModel by viewModels<SetStringVM>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Setup
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) { viewModel.userCancel() }
        // # State
        viewModel.editTextVMItem.bind(vb.et)
        vb.buttonsview.bind(viewModel.buttons) { buttons = it }
    }

    companion object {
        fun navTo(nav: NavController, s: String, parcelableLambdaWrapper: ParcelableLambdaWrapper) {
            nav.navigate(
                R.id.editStringFrag,
                Bundle().apply {
                    putString(KEY1, s)
                    putParcelable(KEY2, parcelableLambdaWrapper)
                }
            )
        }
    }
}