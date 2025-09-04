plugins {
    alias(libs.plugins.miare.android.application)
    alias(libs.plugins.miare.android.application.compose)
    alias(libs.plugins.miare.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "ir.miare.androidcodechallenge"
    compileSdk = 36
    defaultConfig {
        applicationId = "ir.miare.androidcodechallenge"
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.hilt.android)
    implementation(libs.hilt.core)
    implementation(libs.jackson.datatype.joda)
    implementation(libs.jackson.datatype.json.org)
    implementation(libs.jackson.kotlin)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.mockfit.runtime)

    androidTestImplementation(platform(libs.androidx.compose.bom))

    ksp(libs.hilt.compiler)
    ksp(libs.mockfit.compiler)

    testImplementation(libs.junit5.api)
    testRuntimeOnly(libs.junit5.engine)
    testImplementation(libs.junit5.params)

    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotest.framework.api)
    testImplementation(libs.kotest.runner.junit5)
}
