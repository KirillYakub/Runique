plugins {
    alias(libs.plugins.runique.android.feature.ui)
}

android {
    namespace = "com.kiras.analytics.presentation"
}

dependencies {
    implementation(projects.analytics.domain)
}