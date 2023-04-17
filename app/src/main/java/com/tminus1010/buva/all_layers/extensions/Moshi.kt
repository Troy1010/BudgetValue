package com.tminus1010.buva.all_layers.extensions

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.tminus1010.buva.domain.ResetStrategy
import com.tminus1010.buva.domain.ResolutionStrategy
import com.tminus1010.tmcommonkotlin.misc.extensions.fromJson
import com.tminus1010.tmcommonkotlin.misc.extensions.toJson
import java.lang.reflect.Type


inline fun <reified T> getType(): Type = T::class.java

@Suppress("UNCHECKED_CAST")
inline fun <reified T> Moshi.easyToJson(x: T): String {
    return when (getType<T>()) {
        getType<Triple<ResetStrategy?, ResolutionStrategy, ResolutionStrategy>>() -> {
            val type = Types.newParameterizedType(Triple::class.java, getType<ResetStrategy?>(), getType<ResolutionStrategy>(), getType<ResolutionStrategy>())
            adapter<Triple<ResetStrategy?, ResolutionStrategy, ResolutionStrategy>>(type).toJson(x as Triple<ResetStrategy?, ResolutionStrategy, ResolutionStrategy>)
        }
        getType<List<String>>() -> {
            val type = Types.newParameterizedType(List::class.java, String::class.java)
            adapter<List<String>>(type).toJson(x as List<String>)
        }
        else -> toJson(x)
    }
}


inline fun <reified T : Any> Moshi.easyFromJson(s: String): T {
    return when (getType<T>()) {
        getType<Triple<ResetStrategy?, ResolutionStrategy, ResolutionStrategy>>() -> {
            val type = Types.newParameterizedType(Triple::class.java, getType<ResetStrategy?>(), getType<ResolutionStrategy>(), getType<ResolutionStrategy>())
            adapter<Triple<ResetStrategy?, ResolutionStrategy, ResolutionStrategy>>(type).fromJson(s) as T
        }
        getType<List<String>>() -> {
            val type = Types.newParameterizedType(List::class.java, String::class.java)
            adapter<List<String>>(type).fromJson(s) as T
        }
        else -> fromJson(s)
    }
}