package com.tminus1010.buva.ui.host

import androidx.navigation.NavController
import com.tminus1010.buva.ui.all_features.view_model_item.MenuVMItem
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject

open class GetExtraMenuItemPartials @Inject constructor() {
    open operator fun invoke(nav: BehaviorSubject<NavController>) = emptyArray<MenuVMItem>()
}