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
            "testImplementation"(libs.findLibrary("junit5.api").get())
            "testImplementation"(libs.findLibrary("junit5.params").get())
            "testRuntimeOnly"(libs.findLibrary("junit5.engine").get())
            "testImplementation"(libs.findLibrary("kotest.assertions.core").get())
            "testImplementation"(libs.findLibrary("kotest.framework.api").get())
            "testImplementation"(libs.findLibrary("kotest.runner.junit5").get())
            "testImplementation"(libs.findLibrary("mockk").get())
            "testImplementation"(libs.findLibrary("mockk.agent").get())
            "testImplementation"(libs.findLibrary("turbine").get())
            "testImplementation"(libs.findLibrary("okhttp.mockwebserver").get())
        }
    }
}

