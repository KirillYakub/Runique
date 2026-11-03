plugins {
    alias(libs.plugins.splitline.jvm.library)
}

dependencies {
    implementation(projects.core.domain)
}