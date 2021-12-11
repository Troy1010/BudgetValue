package com.tminus1010.budgetvalue._core.presentation.service

import androidx.navigation.NavController
import com.tminus1010.budgetvalue._core.presentation.model.MenuVMItem
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject

open class GetExtraMenuItemPartials @Inject constructor() {
    open operator fun invoke(nav: BehaviorSubject<NavController>) = emptyArray<MenuVMItem>()
}