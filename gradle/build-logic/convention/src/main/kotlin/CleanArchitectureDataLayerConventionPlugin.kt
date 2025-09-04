import ir.miare.androidcodechallenge.buildlogic.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

class CleanArchitectureDataLayerConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        apply(plugin = "miare.android.library")
        apply(plugin = "miare.hilt")
        apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
        dependencies {
            "implementation"(libs.findLibrary("hilt.android").get())
            "implementation"(project(":core:network"))
            "implementation"(project(":core:domain"))
        }
    }
}

