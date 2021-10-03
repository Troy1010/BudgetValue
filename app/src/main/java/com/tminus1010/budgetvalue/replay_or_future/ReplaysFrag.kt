package com.tminus1010.budgetvalue.replay_or_future

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.middleware.view.recipe_factories.itemTextViewRB
import com.tminus1010.budgetvalue._core.middleware.view.viewBinding
import com.tminus1010.budgetvalue.databinding.FragReplaysBinding
import com.tminus1010.budgetvalue.replay_or_future.models.BasicReplay
import com.tminus1010.budgetvalue.transactions.view.ReplayFrag
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
        replaysVM.replays
            .observeOn(Schedulers.computation())
            .map { replays ->
                replays.map { replay ->
                    listOf(
                        itemTextViewRB().create(replay.name) {
                            // TODO("Duct-tape solution b/c ReplayFrag needs categorySelectionVM")
                            nav.navigate(R.id.categorizeNestedGraph)
                            ReplayFrag.navTo(nav, replay as BasicReplay)
                        }
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