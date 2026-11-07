plugins {
    alias(libs.plugins.splitline.jvm.library)
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)

    testImplementation(libs.junit)
}