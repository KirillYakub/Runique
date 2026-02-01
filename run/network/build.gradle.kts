plugins {
    alias(libs.plugins.runique.android.library)
    alias(libs.plugins.runique.android.library.compose)
    alias(libs.plugins.runique.jvm.ktor)
}

android {
    namespace = "com.kiras.run.network"
}

dependencies {
    implementation(libs.bundles.koin)

    implementation(projects.core.domain)
    implementation(projects.core.data)
}