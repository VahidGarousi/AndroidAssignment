plugins {
    alias(libs.plugins.miare.jvm.library)
}



dependencies {
    api(libs.javax.inject)
    implementation(projects.core.common)
    implementation(libs.kotlinx.coroutines.core)
}
