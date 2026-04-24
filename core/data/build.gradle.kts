plugins {
    alias(libs.plugins.forpet.android.library)
    alias(libs.plugins.forpet.hilt)
}

android {
    namespace = "com.forpet.app.core.data"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:database"))
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)
}
