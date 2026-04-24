plugins {
    alias(libs.plugins.forpet.android.library)
    alias(libs.plugins.forpet.android.compose)
}

android {
    namespace = "com.forpet.app.core.designsystem"
}

dependencies {
    // AndroidComposeConventionPlugin adds compose dependencies automatically
}
