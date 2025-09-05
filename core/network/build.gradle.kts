plugins {
    alias(libs.plugins.miare.android.library)
    alias(libs.plugins.miare.hilt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.secrets)
//    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}
android {
    namespace = "ir.miare.core.network"
    buildFeatures {
        buildConfig = true
    }
}

secrets {
    defaultPropertiesFileName = "secrets.defaults.properties"
}


dependencies {
    api(libs.kotlinx.serialization.json)
    api(libs.okhttp.logging)
    api(libs.retrofit.core)
    api(libs.retrofit.kotlin.serialization)
    api(projects.core.common)

    testImplementation(libs.kotlinx.coroutines.test)
}
