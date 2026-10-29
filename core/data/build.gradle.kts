plugins {
    alias(libs.plugins.splitline.android.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.kiras.core.data"
}

dependencies {
    implementation(libs.timber)
    implementation(libs.bundles.koin)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.org.mongodb.bson)

    implementation(projects.core.domain)
    implementation(projects.core.database)
}
