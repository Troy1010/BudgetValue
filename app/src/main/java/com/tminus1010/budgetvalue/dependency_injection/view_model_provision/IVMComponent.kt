package com.tminus1010.budgetvalue.dependency_injection.view_model_provision

import androidx.lifecycle.ViewModel
import com.tminus1010.tmcommonkotlin.logz.logx
import java.lang.reflect.Method
import kotlin.reflect.jvm.kotlinFunction

/**
 * Any dagger components that want reflection to find VMProvisionMethods should inherit this interface
 */
interface IVMComponent

// Must be extension method to avoid dagger compilation.
inline fun <reified VM : ViewModel> IVMComponent.getVMProvisionMethod(): Method {
    return this::class.java.declaredMethods
        .find { it.returnType == VM::class.java }
        .let {
            it ?: error("""
                |Could not find a provisional method for:${VM::class.java.simpleName}
                |Please add this method to the AppComponent:
                |    fun get${VM::class.java.simpleName}(): ${VM::class.java.simpleName}
                |Also, make sure the ViewModel has @Inject constructor()
            """.trimMargin())
        }
}

// Must be extension method to avoid dagger compilation.
fun IVMComponent.getVMProvisionMethods(): List<Method> =
    this::class.java.declaredMethods
        .filter { ViewModel::class.java.isInstance(it.returnType) }