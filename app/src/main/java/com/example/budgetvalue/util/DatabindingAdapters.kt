package com.example.budgetvalue.util

import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.budgetvalue.layers.z_ui.misc.rxBindOneWay
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject

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