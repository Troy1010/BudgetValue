package com.tminus1010.budgetvalue.replay_or_future

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.extensions.easyText
import com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3.IViewItemRecipe3
import com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3.ViewItemRecipeFactory3
import com.tminus1010.budgetvalue._core.middleware.ui.viewBinding
import com.tminus1010.budgetvalue.databinding.FragReplaysBinding
import com.tminus1010.budgetvalue.databinding.ItemTextViewBinding
import com.tminus1010.budgetvalue.replay_or_future.models.IReplay
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.schedulers.Schedulers

@AndroidEntryPoint
class ReplaysFrag : Fragment(R.layout.frag_replays) {
    private val vb by viewBinding(FragReplaysBinding::bind)
    private val replaysVM: ReplaysVM by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //
        replaysVM.navUp.observe(viewLifecycleOwner) { nav.navigateUp() }
        // # TMTableView
        val clickableTextViewRecipeFactory = ViewItemRecipeFactory3<ItemTextViewBinding, IReplay>(
            { ItemTextViewBinding.inflate(LayoutInflater.from(requireContext())) },
            { replay, vb, _ ->
                vb.root.setOnClickListener { replaysVM.userAddSearchTextToReplay(replay) }
                vb.textview.easyText = replay.name
            }
        )
        replaysVM.replays
            .observeOn(Schedulers.computation())
            .map { replays ->
                replays.map { replay ->
                    listOf<IViewItemRecipe3>(
                        clickableTextViewRecipeFactory.createOne(replay),
                    )
                }
            }
            .observe(viewLifecycleOwner) { recipes2D ->
                vb.tmTableView.initialize(
                    recipeGrid = recipes2D,
                    shouldFitItemWidthsInsideTable = true,
                )
            }
    }

    companion object {
        fun navTo(nav: NavController) {
            nav.navigate(R.id.replaysFrag)
        }
    }
}