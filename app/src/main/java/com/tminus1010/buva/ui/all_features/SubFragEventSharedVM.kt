package com.tminus1010.buva.ui.all_features

import androidx.fragment.app.Fragment
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

// How to have a userIntent in a child frag affect the host frag? How to share state/events between VM?
//	Solution 1: Share the VM, either by Activity or NavGraph
//	Solution 2: Make a shared class, either by @Singleton or some other annotation
//	Solution 3: Mediation
//	Solution 4: pass a lambda into fragment?
// .. This is the 2nd solution
// TODO: use solution 4 instead.
@Singleton
class SubFragEventSharedVM @Inject constructor() {
    val showFragment = MutableSharedFlow<Fragment>()
}