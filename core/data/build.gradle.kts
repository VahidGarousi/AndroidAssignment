plugins {
    alias(libs.plugins.miare.android.library)
    alias(libs.plugins.miare.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "ir.miare.core.data"
}

dependencies {
    api(projects.core.common)
    api(projects.core.domain)

    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.kotlinx.serialization.json)
}
