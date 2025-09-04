import com.android.build.gradle.LibraryExtension
import ir.miare.androidcodechallenge.buildlogic.convention.configureAndroidCompose
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.getByType

class AndroidLibraryComposeConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) = with(project) {
        apply(plugin = "com.android.library")
        apply(plugin = "org.jetbrains.kotlin.plugin.compose")

        val extension = extensions.getByType<LibraryExtension>()
        configureAndroidCompose(extension)
    }
}
