// # BuildSrc
ext["kotlin_version"] = "1.6.10"

plugins {
    `kotlin-dsl`
}

repositories {
    mavenLocal()
    google()
    mavenCentral()
}

dependencies {
    implementation("com.android.tools.build:gradle:4.2.2")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.0") // migration to 1.4.31 gave warning: Unsupported Kotlin Version
    implementation(gradleApi())
    implementation(localGroovy())
    //noinspection GradleDynamicVersion
    implementation("com.tminus1010.tmcommonkotlin:tmcommonkotlin-dsl:+")
    implementation("com.google.dagger:hilt-android-gradle-plugin:2.37")
}

gradlePlugin {
    plugins {
        create("BudgetValuePlugin") {
            id = "BudgetValuePlugin"
            implementationClass = "com.tminus1010.budgetvalue.BudgetValuePlugin"
        }
    }
}