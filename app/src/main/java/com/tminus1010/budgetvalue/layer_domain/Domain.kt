package com.tminus1010.budgetvalue.layer_domain

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Domain is the facade to the domain layer.
 * If you ever change the business logic, all other layers will not require updates.
 */
@Singleton
class Domain @Inject constructor(
    private val appInitializer: AppInitializer,
    private val datePeriodGetter: DatePeriodGetter,
    private val typeConverter: TypeConverter,
) : IAppInitializer by appInitializer,
    IDatePeriodGetter by datePeriodGetter,
    ITypeConverter by typeConverter