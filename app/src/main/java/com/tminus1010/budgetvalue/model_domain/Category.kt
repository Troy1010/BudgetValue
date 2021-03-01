package com.tminus1010.budgetvalue.model_domain

import com.tminus1010.budgetvalue.model_data.CategoryDTO

data class Category (
    val name: String,
    val type: Type,
    val isRequired: Boolean = false
) {
    override fun toString() = name // for better logs.
    enum class Type { Misc, Always, Reservoir }
    fun toCategoryDTO() =
        CategoryDTO(name, type.ordinal.toString(), isRequired)
    companion object {
        fun fromDTO(categoryDTO: CategoryDTO) =
            Category(categoryDTO.name, Type.values()[categoryDTO.type.toInt()], categoryDTO.isRequired)
    }
}