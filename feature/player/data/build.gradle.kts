plugins {
    alias(libs.plugins.miare.feature.data)
    alias(libs.plugins.kotlin.serialization)
}


android {
    namespace = "ir.miare.feature.player.data"
}

dependencies {
    implementation(projects.feature.player.domain)
}
