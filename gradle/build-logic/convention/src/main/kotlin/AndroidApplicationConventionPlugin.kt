import com.android.build.api.dsl.ApplicationExtension
import ir.miare.androidcodechallenge.buildlogic.convention.configureKotlinAndroid
import ir.miare.androidcodechallenge.buildlogic.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        apply(plugin = "com.android.application")
        apply(plugin = "org.jetbrains.kotlin.android")
        apply(plugin = "com.squareup.sort-dependencies")

        extensions.configure<ApplicationExtension> {
            configureKotlinAndroid(this)
            defaultConfig.targetSdk =
                Integer.parseInt(libs.findVersion("androidTargetSdk").get().toString())
//            testOptions.animationsDisabled = true
        }
    }
}
