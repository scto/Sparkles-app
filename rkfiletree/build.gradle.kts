
group = "com.github.RohitKushvaha01"
version = "1.0.0"


plugins {
     id("com.android.library") version "8.2.1" apply false
     id("org.jetbrains.kotlin.android") version "2.0.21" apply false
    `maven-publish`
}

android {
    namespace = "com.rk.filetree"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            // Use the correct component for Android libraries (usually "release" or "debug")
            from(components.findByName("release"))
        }
    }
    repositories {
        maven {
            url = uri("https://jitpack.io")
        }
    }
}



dependencies {

    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.13.0-alpha08")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}