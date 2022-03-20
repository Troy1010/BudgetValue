package com.tminus1010.budgetvalue.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tminus1010.budgetvalue.all_features.all_layers.InvalidCategoryNameException
import com.tminus1010.budgetvalue.all_features.all_layers.extensions.easyEmit
import com.tminus1010.budgetvalue.all_features.presentation.model.*
import com.tminus1010.budgetvalue.all_features.data.repo.CategoriesRepo
import com.tminus1010.budgetvalue.categories.domain.CategoriesInteractor
import com.tminus1010.budgetvalue.categories.domain.DeleteCategoryFromActiveDomainUC
import com.tminus1010.budgetvalue.categories.domain.ReplaceCategoryGloballyUC
import com.tminus1010.budgetvalue.all_features.app.model.Category
import com.tminus1010.budgetvalue.all_features.app.model.CategoryType
import com.tminus1010.budgetvalue.transactions.app.AmountFormula
import com.tminus1010.tmcommonkotlin.core.extensions.reflectXY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategorySettingsVM @Inject constructor(
    private val deleteCategoryFromActiveDomainUC: DeleteCategoryFromActiveDomainUC,
    private val categoriesRepo: CategoriesRepo,
    private val categoriesInteractor: CategoriesInteractor,
    private val replaceCategoryGloballyUC: ReplaceCategoryGloballyUC
) : ViewModel() {
    // # View Events
    val originalCategoryName = MutableStateFlow("")
    val isForNewCategory = MutableStateFlow(true)

    // # Internal
    private val originalCategory = originalCategoryName.map { if (it == "") null else categoriesInteractor.parseCategory(it) }.stateIn(viewModelScope, SharingStarted.Eagerly, null)
    private val newCategoryToPush = MutableSharedFlow<Category>()
    private val categoryToPush =
        merge(
            combine(isForNewCategory, originalCategoryName)
            { isForNewCategory, originalCategoryName ->
                if (isForNewCategory)
                    Category("")
                else
                    categoriesInteractor.parseCategory(originalCategoryName)
            },
            newCategoryToPush,
        )
            .stateIn(viewModelScope, SharingStarted.Eagerly, Category(""))

    // # User Intents
    fun userSetCategoryName(s: String) {
        newCategoryToPush.easyEmit(categoryToPush.value.copy(name = s))
    }

    fun userSetCategoryDefaultAmountFormula(amountFormula: AmountFormula) {
        newCategoryToPush.easyEmit(categoryToPush.value.copy(defaultAmountFormula = amountFormula))
    }

    fun userSetCategoryType(categoryType: CategoryType) {
        newCategoryToPush.easyEmit(categoryToPush.value.copy(type = categoryType))
    }

    fun userDeleteCategory() {
        deleteCategoryFromActiveDomainUC(categoryToPush.value).subscribe()
        navUp.easyEmit(Unit)
    }

    fun userSubmit() {
        viewModelScope.launch(CoroutineExceptionHandler { _, e -> errors.easyEmit(e) }) {
            if (categoryToPush.value.name == "" ||
                categoryToPush.value.name.equals(Category.DEFAULT.name, ignoreCase = true) ||
                categoryToPush.value.name.equals(Category.UNRECOGNIZED.name, ignoreCase = true)
            ) throw InvalidCategoryNameException()
            if (originalCategoryName.value != "" && originalCategoryName.value != categoryToPush.value.name)
                replaceCategoryGloballyUC.replaceCategoryGlobally(originalCategory.value!!, categoryToPush.value)
            else
                categoriesRepo.push(categoryToPush.value)
            navUp.easyEmit(Unit)
        }
    }

    // # Presentation Events
    val errors = MutableSharedFlow<Throwable>()
    val navUp = MutableSharedFlow<Unit>()
    val showDeleteConfirmationPopup = MutableSharedFlow<String>()

    // # State
    val title = isForNewCategory.flatMapConcat { if (it) flowOf("Create a new Category") else categoryToPush.map { "Settings (${it.name})" } }
    val optionsRecipeGrid =
        categoryToPush.map { categoryToPush ->
            listOf(
                listOfNotNull(
                    TextVMItem("Name"),
                    TextVMItem("Default Amount"),
                    TextVMItem("Type"),
                ),
                listOfNotNull(
                    EditTextVMItem(
                        text = categoryToPush.name,
                        onDone = { userSetCategoryName(it) },
                    ),
                    AmountFormulaPresentationModel1(
                        amountFormula = this.categoryToPush.map { it.defaultAmountFormula }.stateIn(viewModelScope),
                        onNewAmountFormula = { userSetCategoryDefaultAmountFormula(it) }
                    ),
                    SpinnerVMItem(
                        values = CategoryType.getPickableValues().toTypedArray(),
                        initialValue = categoryToPush.type,
                        onNewItem = { userSetCategoryType(it) }
                    ),
                ),
            ).reflectXY()
        }
    val buttons =
        isForNewCategory.map { isForNewCategory ->
            listOfNotNull(
                if (isForNewCategory)
                    null
                else
                    ButtonVMItem(
                        title = "Delete",
                        onClick = { showDeleteConfirmationPopup.easyEmit(originalCategoryName.value) }
                    ),
                ButtonVMItem(
                    title = "Done",
                    onClick = { userSubmit() }
                ),
            )
        }
}