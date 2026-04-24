plugins {
    alias(libs.plugins.forpet.android.library)
    alias(libs.plugins.forpet.android.compose)
}

android {
    namespace = "com.forpet.app.core.ui"
}

dependencies {
    implementation(project(":core:designsystem"))
    implementation(project(":core:model"))
}
