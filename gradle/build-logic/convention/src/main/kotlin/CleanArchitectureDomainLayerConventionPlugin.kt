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
        }
    }
}
