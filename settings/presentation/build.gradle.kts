plugins {
    alias(libs.plugins.splitline.android.feature.ui)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.kiras.settings.presentation"
}

dependencies {
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)

    implementation(projects.core.domain)
}
