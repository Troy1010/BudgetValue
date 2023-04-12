package com.tminus1010.buva.all_layers.extensions

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonWriter

fun JsonWriter.writeNameAndValue(name: String, value: String) {
    name(name)
    value(value)
}

fun <T> JsonWriter.writeNameAndValue(name: String, value: T, adapter: JsonAdapter<T>) {
    name(name)
    adapter.toJson(this, value)
}

inline fun JsonWriter.writeObject(act: JsonWriter.() -> Unit) {
    beginObject()
    act(this)
    endObject()
}

inline fun JsonWriter.writeArray(act: JsonWriter.() -> Unit) {
    beginArray()
    act(this)
    endArray()
}