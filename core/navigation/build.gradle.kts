plugins {
    alias(libs.plugins.forpet.android.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.forpet.app.core.navigation"
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
}
