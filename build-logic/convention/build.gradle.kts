plugins {
    `kotlin-dsl`
}
group = "com.kiras.splitline.buildlogic"
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
    }
}
dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.android.tools.common)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.room.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("android-application") {
            id = "splitline.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("android-application-compose") {
            id = "splitline.android.application.compose"
            implementationClass = "AndroidApplicationComposeConventionPlugin"
        }
        register("android-library") {
            id = "splitline.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("android-library-compose") {
            id = "splitline.android.library.compose"
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }
        register("android-feature-ui") {
            id = "splitline.android.feature.ui"
            implementationClass = "AndroidFeatureUiConventionPlugin"
        }
        register("androidRoom") {
            id = "splitline.android.room"
            implementationClass = "AndroidRoomConventionPlugin"
        }
        register("jvmLibrary") {
            id = "splitline.jvm.library"
            implementationClass = "JvmLibraryConventionPlugin"
        }
    }
}