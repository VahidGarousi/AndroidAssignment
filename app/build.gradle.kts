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
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.json)


    implementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.compose.runtime)

    implementation(libs.hilt.core)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(libs.jackson.kotlin)
    implementation(libs.jackson.datatype.joda)
    implementation(libs.jackson.datatype.json.org)
    implementation(libs.mockfit.runtime)
    ksp(libs.mockfit.compiler)
}
