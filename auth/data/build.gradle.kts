plugins {
    alias(libs.plugins.runique.android.library)
}

android {
    namespace = "com.kiras.auth.data"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.database)
    implementation(projects.auth.domain)

    implementation(libs.bundles.koin)
    implementation(libs.kotlinx.coroutines.core)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
}
