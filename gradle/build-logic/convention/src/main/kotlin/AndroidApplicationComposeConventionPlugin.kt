import com.android.build.api.dsl.ApplicationExtension
import ir.miare.androidcodechallenge.buildlogic.convention.configureAndroidCompose
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.getByType

class AndroidApplicationComposeConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) = with(project) {
        apply(plugin = "com.android.application")
        apply(plugin = "org.jetbrains.kotlin.plugin.compose")

        val extension = extensions.getByType<ApplicationExtension>()
        configureAndroidCompose(extension)
    }
}
