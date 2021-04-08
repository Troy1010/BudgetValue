package com.tminus1010.budgetvalue.categories.models

data class Category (
    val name: String,
    val type: Type,
    val isRequired: Boolean = false
) {
    override fun toString() = name // for better logs.
    enum class Type { Misc, Always, Reservoir }
    fun toDTO() =
        CategoryDTO(name, type.ordinal.toString(), isRequired)
    companion object {
        fun fromDTO(categoryDTO: CategoryDTO) =
            Category(categoryDTO.name, Type.values()[categoryDTO.type.toInt()], categoryDTO.isRequired)
    }
}