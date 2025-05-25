plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")//habilita el uso de para room ,animaciones, inser, put, delete

}

android {
    namespace = "com.example.loginfuncional2"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.loginfuncional2"
        minSdk = 21
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

dependencies {
    implementation(libs.jbcrypt)
    implementation(libs.androidx.room.runtime)//definimos room para manejo de bd local
    implementation(libs.androidx.room.ktx) //definimos para q kotlin maneje sub rutinas
    kapt(libs.androidx.room.compiler)//genera codigo de bd
    implementation(libs.kotlinx.coroutines.core) //tareas de backgraud
    implementation(libs.kotlinx.coroutines.android) //tareas asincronas
    implementation ("com.google.firebase:firebase-firestore-ktx:24.4.2")
    implementation(libs.firebase.firestore.ktx)
    implementation("com.google.firebase:firebase-auth:22.1.1")
    implementation("com.google.firebase:firebase-firestore:24.8.1")
    implementation("com.google.firebase:firebase-database:20.3.0")
    implementation("com.google.firebase:firebase-auth:22.1.1")
    implementation("com.google.firebase:firebase-firestore-ktx:24.4.2")
    implementation("com.google.firebase:firebase-database:20.3.0")
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
