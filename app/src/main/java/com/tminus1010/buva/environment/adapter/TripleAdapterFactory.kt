package com.tminus1010.buva.environment.adapter

import com.squareup.moshi.*
import com.tminus1010.buva.all_layers.extensions.writeArray
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

object TripleAdapterFactory : JsonAdapter.Factory {

    inline fun <reified T> getType(): Type = T::class.java

    override fun create(type: Type, annotations: MutableSet<out Annotation>, moshi: Moshi): JsonAdapter<*>? {
        if (type !is ParameterizedType) {
            return null
        }

        if (Triple::class.java != type.rawType) {
            return null
        }

        val listType = Types.newParameterizedType(List::class.java, getType<String?>())
        val listAdapter = moshi.adapter<List<String?>>(listType)

        return TripleAdapter(moshi.adapter(type.actualTypeArguments[0]), moshi.adapter(type.actualTypeArguments[1]), moshi.adapter(type.actualTypeArguments[2]), listAdapter)
    }

    private class TripleAdapter(
        private val firstAdapter: JsonAdapter<Any?>,
        private val secondAdapter: JsonAdapter<Any?>,
        private val thirdAdapter: JsonAdapter<Any?>,
        private val listAdapter: JsonAdapter<List<String?>>,
    ) : JsonAdapter<Triple<Any?, Any?, Any?>>() {
        override fun fromJson(reader: JsonReader): Triple<Any?, Any?, Any?>? {
            val list = listAdapter.fromJson(reader) ?: return null

            require(list.size == 3) {
                "triple with more or less than three elements: $list"
            }

            val first = firstAdapter.fromJsonValue(list[0])
            val second = secondAdapter.fromJsonValue(list[1])
            val third = thirdAdapter.fromJsonValue(list[2])
            return Triple(first, second, third)
        }

        override fun toJson(writer: JsonWriter, value: Triple<Any?, Any?, Any?>?) {
            if (value == null) {
                val qwer: String? = null
                writer.value(qwer)
            } else
                writer.writeArray {
                    firstAdapter.toJson(writer, value.first)
                    secondAdapter.toJson(writer, value.second)
                    thirdAdapter.toJson(writer, value.third)
                }
        }
    }
}