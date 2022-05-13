plugins {
    id("common-lib")
}

android {
    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    // kotlin runtime
    implementation(Deps.kotlinStdlib)
    implementation(Deps.KOTLIN_COROUTINES_ANDROID)

    // support libs
    implementation(Deps.xCore)
    implementation(Deps.X_RECYCLERVIEW)
    implementation(Deps.X_APPCOMPAT)

    // testing libs
    testImplementation("junit:junit:4.13")
    androidTestImplementation("androidx.test:runner:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
}

repositories {
    mavenCentral()
}

tasks.withType(Javadoc::class).all {
    enabled = false // <--- https://github.com/novoda/bintray-release/issues/71
}