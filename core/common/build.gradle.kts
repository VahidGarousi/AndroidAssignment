plugins {
    alias(libs.plugins.miare.jvm.library)
    alias(libs.plugins.miare.hilt)
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
}