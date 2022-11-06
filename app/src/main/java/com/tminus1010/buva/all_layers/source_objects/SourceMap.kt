package com.tminus1010.buva.all_layers.source_objects

import com.tminus1010.tmcommonkotlin.core.extensions.removeIf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking

class SourceMap<K, V : Any>(coroutineScope: CoroutineScope, private val _map: MutableMap<K, V> = emptyMap<K, V>().toMutableMap(), val exitValue: V? = null) : MutableMap<K, V> by _map {
    constructor(coroutineScope: CoroutineScope, exitValue: V?, vararg entries: Pair<K, V>) : this(coroutineScope, entries.associate { it.first to it.second }.toMutableMap(), exitValue)

    private val change = MutableSharedFlow<Change<K, V>>()

    @Suppress("UNCHECKED_CAST")
    val itemFlowMap =
        change
            .scan(_map.mapValues { MutableStateFlow(it.value) })
            { acc, v ->
                when (v.type) {
                    AddRemEditType.REMOVE -> (acc as MutableMap<K, MutableStateFlow<V>>).remove(v.key)
                    AddRemEditType.ADD -> (acc as MutableMap<K, MutableStateFlow<V>>)[v.key] = MutableStateFlow(v.value)
                    AddRemEditType.EDIT -> (acc as MutableMap<K, MutableStateFlow<V>>)[v.key]!!.emit(v.value)
                }
                acc
            }
            .stateIn(coroutineScope, SharingStarted.Eagerly, _map.mapValues { MutableStateFlow(it.value) })
    val map =
        change.map { _map }
            .stateIn(coroutineScope, SharingStarted.Eagerly, _map)

    fun adjustTo(newMap: Map<K, V>) {
        // If any keys should be removed, remove them.
        this.removeIf { it.key !in newMap }
        // If any values do not match, update them.
        newMap.filter { (k, v) -> k !in this || v != this[k] }.forEach { (k, v) -> this[k] = v }
    }

    override fun put(key: K, value: V): V? {
        runBlocking {
            change.emit(
                Change(
                    type = when {
                        key in _map -> AddRemEditType.EDIT
                        else -> AddRemEditType.ADD
                    },
                    key = key,
                    value = value
                )
            )
        }
        return _map.put(key, value)
    }

    override fun putAll(from: Map<out K, V>) {
        from.forEach { (k, v) -> put(k, v) }
    }

    override fun clear() {
        _map.forEach { (k, v) -> remove(k) }
    }

    override fun remove(key: K): V? {
        val originalResult = _map.remove(key)
        runBlocking {
            if (exitValue != null && key in map.value)
                change.emit(
                    Change(
                        type = AddRemEditType.EDIT,
                        key = key,
                        value = exitValue
                    )
                )
            change.emit(
                Change(
                    type = AddRemEditType.REMOVE,
                    key = key,
                    value = map.value[key]!! // TODO: This seems unnecessary
                )
            )
        }
        return originalResult
    }
}
