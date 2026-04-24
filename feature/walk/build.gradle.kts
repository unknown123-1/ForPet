plugins {
    alias(libs.plugins.forpet.android.library)
    alias(libs.plugins.forpet.android.compose)
    alias(libs.plugins.forpet.hilt)
}

android {
    namespace = "com.forpet.app.feature.walk"
}

dependencies {
    implementation(project(":core:designsystem"))
    implementation(project(":core:model"))
    implementation(project(":core:data"))
    implementation(project(":core:navigation"))
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)

    // Google Maps
    implementation(libs.maps.compose)
    implementation(libs.play.services.maps)
}
