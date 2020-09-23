package com.example.budgetvalue.layer_ui.misc

import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal


object DatabindingAdapters {
    @BindingAdapter("android:text")
    @JvmStatic
    fun setText(view: TextView, value: BigDecimal?) {
        view.text = value.toString()
    }

    @InverseBindingAdapter(attribute = "android:text")
    @JvmStatic
    fun getText(view: TextView): BigDecimal {
        return view.text.toString().toBigDecimal()
    }

    @BindingAdapter("rxBindOneWay2")
    @JvmStatic
    fun rxBindOneWay2(
        textView: TextView,
        observable: Observable<String?>
    ) {
        textView.rxBindOneWay(observable)
    }
}