// build.gradle.kts (Módulo: app)
// Este arquivo fica na raiz do seu projeto (não dentro da pasta 'app')

plugins {

    id("com.android.application") version "8.1.0" apply false // Use a versão estável mais recente
    id("org.jetbrains.kotlin.android") version "1.9.20" apply false // Versão do Kotlin
}
// ...

android {
    // ... (Seu namespace e configurações de SDK)
    compileSdk = 34

    defaultConfig {
        // ...
    }

    // Configurações do Compose
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4" // Ajuste conforme sua versão do Kotlin
    }

    // ... (Compile Options e Kotlin Options)
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2023.03.00")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    // Core AndroidX
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")

    // Corrotinas
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // ViewModel e LiveData para Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")

    // Retrofit (Rede) + Gson (Conversor JSON)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Coil para carregar Imagens no Compose
    implementation("io.coil-kt:coil-compose:2.4.0")

    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.7.5")

    // Activity Compose
    implementation("androidx.activity:activity-compose:1.8.1")

    // Testes e Debug
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(composeBom)
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}