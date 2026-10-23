plugins {
    alias(libs.plugins.runique.android.feature.ui)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.kiras.auth.presentation"
}

dependencies {
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)

    implementation(projects.core.domain)
    implementation(projects.auth.domain)
}
