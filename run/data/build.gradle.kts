plugins {
    alias(libs.plugins.runique.android.library)
}

android {
    namespace = "com.kiras.run.data"
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.bundles.koin)
    implementation(libs.google.android.gms.play.services.location)

    implementation(projects.core.domain)
    implementation(projects.core.database)
    implementation(projects.run.domain)
}
