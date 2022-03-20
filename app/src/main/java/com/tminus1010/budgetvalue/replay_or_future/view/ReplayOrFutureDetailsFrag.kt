package com.tminus1010.budgetvalue.replay_or_future.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.KEY1
import com.tminus1010.budgetvalue._core.all.extensions.easyEmit
import com.tminus1010.budgetvalue._core.data.MoshiWithCategoriesProvider
import com.tminus1010.budgetvalue._core.framework.view.viewBinding
import com.tminus1010.budgetvalue.databinding.FragCreateFutureBinding
import com.tminus1010.budgetvalue.replay_or_future.domain.BasicFuture
import com.tminus1010.budgetvalue.replay_or_future.domain.BasicReplay
import com.tminus1010.budgetvalue.replay_or_future.domain.IReplayOrFuture
import com.tminus1010.budgetvalue.replay_or_future.presentation.ReplayOrFutureDetailsVM
import com.tminus1010.tmcommonkotlin.core.tryOrNull
import com.tminus1010.tmcommonkotlin.coroutines.extensions.observe
import com.tminus1010.tmcommonkotlin.misc.extensions.bind
import com.tminus1010.tmcommonkotlin.misc.extensions.fromJson
import com.tminus1010.tmcommonkotlin.misc.extensions.toJson
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@AndroidEntryPoint
class ReplayOrFutureDetailsFrag : Fragment(R.layout.frag_create_future) {
    val vb by viewBinding(FragCreateFutureBinding::bind)
    val vm by viewModels<ReplayOrFutureDetailsVM>()

    @Inject
    lateinit var moshiWithCategoriesProvider: MoshiWithCategoriesProvider

    val replayOrFuture: IReplayOrFuture
        get() = requireArguments().getString(KEY1).let {
            tryOrNull { moshiWithCategoriesProvider.moshi.fromJson<BasicFuture>(it)!! }
                ?: tryOrNull { moshiWithCategoriesProvider.moshi.fromJson<BasicReplay>(it)!! }
                ?: error("Oh no!")
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Setup
        vm.replayOrFuture.easyEmit(replayOrFuture)
        // # Events
        vm.navUp.observe(viewLifecycleOwner) { nav.navigateUp() }
        vm.navToCategorySelection.observe(viewLifecycleOwner) { nav.navigate(R.id.selectCategoriesFrag) }
        vm.navToChooseTransaction.observe(viewLifecycleOwner) { nav.navigate(R.id.chooseTransactionFrag) }
        vm.navToSetSearchTexts.observe(viewLifecycleOwner) { nav.navigate(R.id.setSearchTextsFrag) }
        // # State
        vb.tmTableViewOtherInput.bind(vm.otherInput) {
            initialize(
                recipeGrid = it.map { it.map { it.toViewItemRecipe(requireContext()) } },
                shouldFitItemWidthsInsideTable = true,
            )
        }
        vb.tmTableViewCategoryAmounts.bind(combine(vm.recipeGrid, vm.dividerMap) { a, b -> Pair(a, b) }) { (recipeGrid, dividerMap) ->
            initialize(
                recipeGrid = recipeGrid.map { it.map { it.toViewItemRecipe(requireContext()) } },
                dividerMap = dividerMap.mapValues { it.value.toViewItemRecipe(requireContext()) },
                shouldFitItemWidthsInsideTable = true,
            )
        }
        vb.buttonsview.bind(vm.buttons) { buttons = it }
    }

    companion object {
        fun navTo(nav: NavController, moshiWithCategoriesProvider: MoshiWithCategoriesProvider, iReplayOrFuture: IReplayOrFuture) {
            nav.navigate(R.id.futureDetailsFrag, Bundle().apply {
                putString(KEY1,
                    when (iReplayOrFuture) {
                        is BasicFuture -> moshiWithCategoriesProvider.moshi.toJson(iReplayOrFuture)
                        is BasicReplay -> moshiWithCategoriesProvider.moshi.toJson(iReplayOrFuture)
                        else -> error("Unhandled type:$iReplayOrFuture")
                    }
                )
            })
        }
    }
}