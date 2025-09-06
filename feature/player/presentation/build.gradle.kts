plugins {
    alias(libs.plugins.miare.feature.presentation)
}

android {
    namespace = "ir.miare.feature.player.presentation"
}


dependencies {
    implementation(projects.feature.player.domain)
}