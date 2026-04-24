plugins {
    alias(libs.plugins.forpet.android.library)
    alias(libs.plugins.forpet.android.compose)
    alias(libs.plugins.forpet.hilt)
}

android {
    namespace = "com.forpet.app.feature.mypet"
}

dependencies {
    implementation(project(":core:designsystem"))
    implementation(project(":core:ui"))
    implementation(project(":core:model"))
    implementation(project(":core:navigation"))
    implementation(project(":core:data"))
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.coil.compose)
    implementation(libs.androidx.exifinterface)
}
