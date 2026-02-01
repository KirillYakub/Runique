import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.DynamicFeatureExtension
import com.kiras.convention.ExtensionType
import com.kiras.convention.addUiLayerDependency
import com.kiras.convention.configureAndroidCompose
import com.kiras.convention.configureBuildTypes
import com.kiras.convention.configureKotlinAndroid
import com.kiras.convention.libs
import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

class AndroidDynamicFeatureConventionPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            pluginManager.run {
                apply("org.jetbrains.kotlin.plugin.compose")
                apply("com.android.dynamic-feature")
                apply("org.jetbrains.kotlin.android")
            }
            extensions.configure<DynamicFeatureExtension> {
                configureKotlinAndroid(this)
                configureAndroidCompose(this)
                configureBuildTypes(this, ExtensionType.DYNAMIC_FEATURE)
            }
            dependencies {
                addUiLayerDependency(target)
                "testImplementation"(kotlin("test"))
            }
        }
    }
}