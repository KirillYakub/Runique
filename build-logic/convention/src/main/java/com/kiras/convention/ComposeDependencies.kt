package com.kiras.convention

import org.gradle.api.Project
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.project

fun DependencyHandlerScope.addUiLayerDependency(project: Project) {
    add("implementation", project(":core:presentation:ui"))
    add("implementation", project(":core:presentation:designsystem"))

    add("implementation", project.libs.findBundle("koin.compose").get())

    add("implementation", project.libs.findBundle("compose").get())
    add("debugImplementation", project.libs.findBundle("compose.debug").get())
    add("androidTestImplementation", project.libs.findLibrary("androidx.compose.ui.test.junit4").get())
}