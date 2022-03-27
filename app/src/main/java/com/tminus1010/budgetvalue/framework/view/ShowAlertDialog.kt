package com.tminus1010.budgetvalue.framework.view

import android.app.Activity
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.tminus1010.budgetvalue.all_layers.extensions.easyText3
import com.tminus1010.budgetvalue.framework.launchOnMainThread
import com.tminus1010.tmcommonkotlin.view.NativeText
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Must use [Activity] or you will see: java.lang.IllegalStateException: You need to use a Theme.AppCompat theme (or descendant) with this activity.
 */
class ShowAlertDialog constructor(private val activity: Activity) {
    /**
     * EditText
     */
    suspend operator fun invoke(body: String, initialText: CharSequence? = null, onYes: ((CharSequence?) -> Unit)? = null, onNo: (() -> Unit)? = null) = invoke(NativeText.Simple(body), initialText, onYes, onNo)
    suspend operator fun invoke(body: NativeText, initialText: CharSequence? = null, onYes: ((CharSequence?) -> Unit)? = null, onNo: (() -> Unit)? = null) = suspendCoroutine<Unit> { downstream ->
        launchOnMainThread {
            val editText = EditText(activity)
            editText.easyText3 = initialText
            AlertDialog.Builder(activity)
                .setMessage(body.toCharSequence(activity))
                .setView(editText)
                .setPositiveButton("Submit") { _, _ -> onYes?.invoke(editText.easyText3) }
                .setNegativeButton("Cancel") { _, _ -> onNo?.invoke() }
                .setOnDismissListener { downstream.resume(Unit) }
                .show()
        }
    }


    /**
     * Yes/no
     */
    suspend operator fun invoke(body: String, onYes: (() -> Unit)? = null, onNo: (() -> Unit)? = null) = invoke(NativeText.Simple(body), onYes, onNo)
    suspend operator fun invoke(body: NativeText, onYes: (() -> Unit)? = null, onNo: (() -> Unit)? = null) = suspendCoroutine<Unit> { downstream ->
        launchOnMainThread {
            AlertDialog.Builder(activity)
                .setMessage(body.toCharSequence(activity))
                .setPositiveButton("Yes") { _, _ -> onYes?.invoke() }
                .setNegativeButton("No") { _, _ -> onNo?.invoke() }
                .setOnDismissListener { downstream.resume(Unit) }
                .show()
        }
    }


    /**
     * Okay
     */
    suspend operator fun invoke(body: String) = invoke(NativeText.Simple(body))
    suspend operator fun invoke(body: NativeText) = suspendCoroutine<Unit> { downstream ->
        launchOnMainThread {
            AlertDialog.Builder(activity)
                .setMessage(body.toCharSequence(activity))
                .setPositiveButton("Okay") { _, _ -> }
                .setOnDismissListener { downstream.resume(Unit) }
                .show()
        }
    }

}