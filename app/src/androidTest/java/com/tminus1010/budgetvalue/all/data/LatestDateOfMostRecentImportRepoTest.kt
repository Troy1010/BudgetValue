package com.tminus1010.budgetvalue.all.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tminus1010.budgetvalue._core.all.dependency_injection.MiscModule
import com.tminus1010.budgetvalue.__core_testing.app
import com.tminus1010.budgetvalue.all.data.repos.LatestDateOfMostRecentImport
import dagger.hilt.android.testing.HiltAndroidTest
import io.reactivex.rxjava3.observers.TestObserver
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.util.concurrent.TimeUnit

// TODO("Move into TMCommonKotlin and delete")
fun <T> TestObserver<T>.assertLastValue(predicate: (T) -> Boolean) {
    assertValueAt(values().count() - 1) { predicate.invoke(it) }
}

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class LatestDateOfMostRecentImportRepoTest {
    @Test
    fun mostRecentImportDate() {
        // # Given
        val mostRecentImportDate = LatestDateOfMostRecentImport(app, MiscModule.provideMoshi())
        val localDate1 = LocalDate.of(2020, 6, 1)
        val localDate2 = LocalDate.of(1867, 1, 30)
        // # When
        mostRecentImportDate.set(localDate1)
        mostRecentImportDate.test().apply { await(2, TimeUnit.SECONDS) }
                // # Then
            .assertNoErrors().assertValue { (it) -> logz("a:$it"); it == localDate1 }
        // # When
        val tester = mostRecentImportDate.test()
        mostRecentImportDate.set(localDate2)
        tester
            .apply { await(2, TimeUnit.SECONDS) }
            // # Then
            .assertNoErrors().assertLastValue { (it) -> logz("b:$it"); it == localDate2 }
    }
}