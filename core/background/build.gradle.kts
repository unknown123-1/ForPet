plugins {
    alias(libs.plugins.forpet.android.library)
    alias(libs.plugins.forpet.hilt)
}

android {
    namespace = "com.forpet.app.core.background"
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:model"))
    implementation(libs.play.services.location)
}
