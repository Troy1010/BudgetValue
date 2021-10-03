package com.tminus1010.budgetvalue.reconcile.view

//@AndroidEntryPoint
//class AnytimeReconciliationFrag_OLD : Fragment(R.layout.frag_reconcile) {
//    private val vb by viewBinding(FragReconcileBinding::bind)
//    private val anytimeReconciliationVM: AnytimeReconciliationVM by activityViewModels()
//    private val categoriesVM: CategoriesVM by activityViewModels()
//    private val activePlanVM: ActivePlanVM by activityViewModels()
//    private val transactionsMiscVM: TransactionsMiscVM by activityViewModels()
//    private val accountsVM: AccountsVM by activityViewModels()
//    private val budgetedVM: BudgetedVM by activityViewModels()
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        // # Bind Incoming from Presentation layer
//        // ## State
//        vb.buttonsview.buttons = anytimeReconciliationVM.buttons
//        // ## TMTableView
//        val numberedHeaderRecipeFactory = ViewItemRecipeFactory3<ItemHeaderIncomeBinding, Pair<String, Observable<String>>>(
//            { ItemHeaderIncomeBinding.inflate(LayoutInflater.from(context)) },
//            { d, vb, lifecycle ->
//                vb.textviewHeader.text = d.first
//                d.second.observe(lifecycle) { vb.textviewNumber.text = it }
//            }
//        )
//        val reconcileCARecipeFactory = ViewItemRecipeFactory3<ItemMoneyEditTextBinding, Pair<Category, Observable<String>?>>(
//            { ItemMoneyEditTextBinding.inflate(LayoutInflater.from(context)) },
//            { (category, d), vb, lifecycle ->
//                vb.moneyedittext.onDone { anytimeReconciliationVM.pushActiveReconcileCA(category, it) }
//                if (d == null) return@ViewItemRecipeFactory3
//                d.observe(lifecycle) { vb.moneyedittext.easyText = it }
//            }
//        )
//        val budgetedRecipeFactory = ViewItemRecipeFactory3<ItemTextViewBinding, Observable<BigDecimal>?>(
//            { ItemTextViewBinding.inflate(LayoutInflater.from(requireContext())) },
//            { d, vb, lifecycle ->
//                if (d == null) return@ViewItemRecipeFactory3
//                vb.textview.bind(d, lifecycle) {
//                    easyText = it.toString()
//                    if (it < BigDecimal.ZERO)
//                        setTextColor(context.theme.getColorByAttr(R.attr.colorOnError))
//                    else
//                        setTextColor(context.theme.getColorByAttr(R.attr.colorOnBackground))
//                }
//            },
//        )
//        Rx.combineLatest(categoriesVM.userCategories, activePlanVM.activePlanCAs, transactionsMiscVM.currentSpendBlockCAs, anytimeReconciliationVM.activeReconcileCAsToShow, budgetedVM.categoryAmounts)
//            .observeOn(Schedulers.computation())
//            .debounce(100, TimeUnit.MILLISECONDS)
//            .map { (categories, activePlanCAs, currentSpendBlockCAs, activeReconciliationCAs, budgetedCA) ->
//                val recipeGrid = listOf(
//                    listOf(itemHeaderRF().create("Category"))
//                            + itemTextViewRB().create("Default")
//                            + categories.map { itemTextViewRB().create(it.name) },
//                    listOf(numberedHeaderRecipeFactory.createOne(Pair("Plan", activePlanVM.expectedIncome)))
//                            + itemTextViewRB().create(activePlanVM.defaultAmount)
//                            + categories.map { itemTextViewRB().create(activePlanCAs[it] ?: Observable.just("")) },
//                    listOf(itemHeaderRF().create("Actual"))
//                            + itemTextViewRB().create("")
//                            + categories.map { itemTextViewRB().create(currentSpendBlockCAs[it]?.toString() ?: "") },
//                    listOf(itemHeaderRF().create("Reconcile"))
//                            + itemTextViewRB().create(anytimeReconciliationVM.defaultAmount)
//                            + reconcileCARecipeFactory.createMany(categories.map { it to activeReconciliationCAs[it] }),
//                    listOf(numberedHeaderRecipeFactory.createOne(Pair("Budgeted", accountsVM.accountsTotal)))
//                            + itemTextViewRB().create(budgetedVM.defaultAmount)
//                            + budgetedRecipeFactory.createMany(categories.map { budgetedCA[it] })
//                ).reflectXY()
//                val dividerMap = categories
//                    .withIndex()
//                    .distinctUntilChangedWith(compareBy { it.value.type })
//                    .associate { it.index to itemTitledDividerRB().create(it.value.type.name) }
//                    .mapKeys { it.key + 2 } // header row, default row
//                Pair(recipeGrid, dividerMap)
//            }
//            .observe(viewLifecycleOwner) { (recipeGrid, dividerMap) ->
//                vb.tmTableView.initialize(
//                    recipeGrid = recipeGrid,
//                    shouldFitItemWidthsInsideTable = true,
//                    dividerMap = dividerMap,
//                    rowFreezeCount = 1,
//                )
//            }
//    }
//
//    companion object {
//        fun navTo(nav: NavController) {
//            nav.navigate(R.id.reconcileFrag)
//        }
//    }
//}
