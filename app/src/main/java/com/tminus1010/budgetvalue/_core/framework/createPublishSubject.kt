package com.tminus1010.budgetvalue._middleware.framework

import io.reactivex.rxjava3.subjects.PublishSubject

@JvmName("createUnitPublishSubject")
fun createPublishSubject() = PublishSubject.create<Unit>()
fun <T> createPublishSubject() = PublishSubject.create<T>()