plugins {
    alias(libs.plugins.miare.android.library.compose)
    alias(libs.plugins.miare.android.library)
    alias(libs.plugins.miare.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "ir.miare.core.ui"
}


dependencies {
    api(projects.core.common)
    api(projects.core.designsystem)
    api(projects.core.domain)
}
