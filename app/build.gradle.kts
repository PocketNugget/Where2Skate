plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
    id("com.google.gms.google-services") // Aplica el plugin. La versión está en el build.gradle.kts del proyecto.
    id("kotlin-parcelize")
}

android {
    namespace = "com.example.where2skate"
    compileSdk = 35 // Considera usar la última API estable (ej. 34) si encuentras problemas con la preview.

    defaultConfig {
        applicationId = "com.example.where2skate"
        minSdk = 28
        targetSdk = 35 // Ídem compileSdk
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        // Necesitarás una clave de API de Maps. Asegúrate de que esté en tu secrets.properties
        // y que el plugin de secrets esté configurado.
        // Por ejemplo, en tu AndroidManifest.xml:
        // <meta-data android:name="com.google.android.geo.API_KEY" android:value="${MAPS_API_KEY}"/>
        // Y en build.gradle, dentro de defaultConfig, si no usas el plugin de secrets directamente para esto:
        // resValue "string", "maps_api_key", (findProperty("MAPS_API_KEY") ?: "")
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
    buildFeatures {
        compose = true
        // viewBinding = true // No es necesario si solo usas Compose para las UI. Puedes quitarlo.
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1" // Asegúrate que esta versión es compatible con tu versión de Kotlin y Compose. Revisa las tablas de compatibilidad.
    }
}

dependencies {
    // Firebase BoM (Bill of Materials) - Importa solo una vez y la más reciente que uses
    implementation(platform("com.google.firebase:firebase-bom:33.1.0")) // Ejemplo, verifica la última versión estable

    // Firebase
    implementation("com.google.firebase:firebase-analytics-ktx") // KTX para Kotlin
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")

    // AndroidX Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation("com.google.accompanist:accompanist-permissions:0.34.0") // O la versión más reciente
    implementation("androidx.compose.material:material-icons-core:1.6.7") // O la versión que estés usando
    implementation("androidx.compose.material:material-icons-extended:1.6.7") // O la versión que estés usando
    // Jetpack Compose
    implementation(platform(libs.androidx.compose.bom)) // Usa el BoM de Compose
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.activity.compose)
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0") // ViewModel para Compose
    implementation("androidx.navigation:navigation-compose:2.7.7") // Navegación para Compose

    // Google Maps
    implementation(libs.play.services.maps) // El SDK de Maps base
    implementation("com.google.maps.android:maps-compose:4.3.3") // Google Maps para Compose

    // Credential Manager (para Google Sign-In mejorado, opcional pero recomendado)
    implementation("androidx.credentials:credentials:1.3.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.3.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")

    // Coroutines para Play Services (útil para fusedLocationClient y tareas de Firebase)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")
    implementation(libs.play.services.location)

    // Dependencias de AppCompat y ConstraintLayout no son estrictamente necesarias si toda la UI es Compose
    // implementation(libs.androidx.appcompat) // Puedes quitarla si no usas Activities basadas en AppCompat directamente para UI
    // implementation(libs.androidx.constraintlayout) // Puedes quitarla si no usas ConstraintLayout en XML

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)


    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.foundation:foundation") // Incluye Spacer
    implementation("androidx.compose.foundation:foundation-layout") // Incluye Row, Column, width
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.material:material-icons-extended")


}