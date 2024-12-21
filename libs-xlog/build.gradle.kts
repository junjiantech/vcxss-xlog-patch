import com.android.build.gradle.internal.api.BaseVariantOutputImpl

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.vcxss.xlog.patch"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
        version = "0.0.1-alpha.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    libraryVariants.configureEach {
        outputs.configureEach {
            val output = this as BaseVariantOutputImpl
            if (output.outputFileName.endsWith(".aar")) {
                output.outputFileName = "xlog-patch_${version}.aar"
            }
        }
    }
}

dependencies {
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

tasks.register<Jar>("androidSourcesJar") {
    archiveClassifier.set("sources")
    from(android.sourceSets.getByName("main").java.srcDirs) // Full Sources
}

tasks.register<Copy>("copyJar") {
    from(
        "build/intermediates/compile_library_classes_jar/release/bundleLibCompileToJarRelease",
    )
    into("build/app-classes")
    include("classes.jar")
}

// Merge the app classes and the library classes into classes.jar
tasks.register<Jar>("makeJar") {
    // Duplicates cause hard to catch errors, better to fail at compile time.
    duplicatesStrategy = DuplicatesStrategy.FAIL
    dependsOn(tasks.getByName("copyJar"))
    from(zipTree("build/app-classes/classes.jar"))
    destinationDirectory.set(layout.buildDirectory)
    archiveFileName.set("xlog-patch.jar")
}