import ir.miare.androidcodechallenge.buildlogic.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.support.delegates.DependencyHandlerDelegate

class CleanArchitectureDomainLayerConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) = with(project) {
        apply(plugin = "miare.jvm.library")
        dependencies {
            // TODO: add core modules in here later
            add("implementation", project(":core:common"))
            add("implementation", project(":core:domain"))
            add("testImplementation", libs.findLibrary("junit5.api").get())
            add("testImplementation", libs.findLibrary("junit5.params").get())
            add("testRuntimeOnly", libs.findLibrary("junit5.engine").get())
            add("testImplementation", libs.findLibrary("kotest.assertions.core").get())
            add("testImplementation", libs.findLibrary("kotest.framework.api").get())
            add("testImplementation", libs.findLibrary("kotest.runner.junit5").get())
            add("testImplementation", libs.findLibrary("mockk").get())
            add("testImplementation", libs.findLibrary("mockk.agent").get())
            add("testImplementation", libs.findLibrary("turbine").get())
        }
    }
}
