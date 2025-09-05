import ir.miare.androidcodechallenge.buildlogic.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class CleanArchitecturePresentationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply {
            apply("miare.android.library")
            apply("miare.android.library.compose")
            apply("miare.hilt")
            apply("org.jetbrains.kotlin.plugin.serialization")
        }
        dependencies {
            // TODO: add core modules in here later
            "implementation"(project(":core:common"))
            "implementation"(project(":core:domain"))
            "implementation"(project(":core:designsystem"))
            "implementation"(project(":core:ui"))
            "implementation"(libs.findLibrary("androidx.hilt.navigation.compose").get())
            "implementation"(libs.findLibrary("androidx.lifecycle.runtimeCompose").get())
            "implementation"(libs.findLibrary("androidx.lifecycle.viewModelCompose").get())
            "implementation"(libs.findLibrary("androidx.navigation.compose").get())
            "implementation"(libs.findLibrary("kotlinx.serialization.json").get())

            "testImplementation"(libs.findLibrary("androidx.navigation.testing").get())
            "androidTestImplementation"(
                libs.findLibrary("androidx.lifecycle.runtimeTesting").get(),
            )

            "testImplementation"(libs.findLibrary("junit5.api").get())
            "testImplementation"(libs.findLibrary("junit5.params").get())
            "testRuntimeOnly"(libs.findLibrary("junit5.engine").get())
            "testImplementation"(libs.findLibrary("kotest.assertions.core").get())
            "testImplementation"(libs.findLibrary("kotest.framework.api").get())
            "testImplementation"(libs.findLibrary("kotest.runner.junit5").get())
            "testImplementation"(libs.findLibrary("mockk").get())
            "testImplementation"(libs.findLibrary("mockk.agent").get())
            "testImplementation"(libs.findLibrary("turbine").get())
        }
    }
}
