// # BuildSrc
ext["kotlin_version"] = "1.6.20"

plugins {
    id("groovy")
    `kotlin-dsl`
}

repositories {
    mavenLocal()
    google()
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation("com.android.tools.build:gradle:7.4.2")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.20")
    implementation(gradleApi())
    implementation(localGroovy())
    implementation("com.github.Troy1010.TMCommonKotlin:tmcommonkotlin-dsl:1.8.3")
    implementation("com.google.dagger:hilt-android-gradle-plugin:2.41")
}

gradlePlugin {
    plugins {
        create("BuvaPlugin") {
            id = "BuvaPlugin"
            implementationClass = "com.tminus1010.buva.BuvaPlugin"
        }
    }
}