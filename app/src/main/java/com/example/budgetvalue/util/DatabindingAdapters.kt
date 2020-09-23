package com.example.budgetvalue.util

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.budgetvalue.layer_ui.misc.rxBindOneWay
import io.reactivex.rxjava3.core.Observable

object DatabindingAdapters {
    @BindingAdapter("rxBindOneWay2")
    @JvmStatic
    fun rxBindOneWay2(
        textView: TextView,
        observable: Observable<String?>
    ) {
        textView.rxBindOneWay(observable)
    }
}