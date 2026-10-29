plugins {
    alias(libs.plugins.splitline.android.library)
    alias(libs.plugins.splitline.android.room)
}

android {
    namespace = "com.kiras.statistics.data"
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.bundles.koin)

    implementation(projects.core.database)
    implementation(projects.core.domain)
    implementation(projects.statistics.domain)
}