import com.kiras.convention.configureKotlinJvm
import com.kiras.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class JvmKtorConventionPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            pluginManager.run {
                apply("org.jetbrains.kotlin.plugin.serialization")
            }
            dependencies {
                add("implementation", libs.findBundle("ktor").get())
            }
        }
    }
}