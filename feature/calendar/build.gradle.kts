plugins {
    alias(libs.plugins.forpet.android.library)
    alias(libs.plugins.forpet.android.compose)
    alias(libs.plugins.forpet.hilt)
}

android {
    namespace = "com.forpet.app.feature.calendar"
}

dependencies {
    implementation(project(":core:designsystem"))
    implementation(project(":core:ui"))
    implementation(project(":core:model"))
    implementation(project(":core:data"))

    implementation(libs.calendar.compose)
    implementation(libs.androidx.hilt.navigation.compose)
}
