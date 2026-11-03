plugins {
    alias(libs.plugins.splitline.android.library)
    alias(libs.plugins.splitline.android.room)
}

android {
    namespace = "com.kiras.core.database"
}

dependencies {
    implementation(libs.org.mongodb.bson)
    implementation(libs.bundles.koin)

    implementation(projects.core.domain)
}