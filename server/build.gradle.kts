plugins {
    alias(libs.plugins.kotlinJvm)
    application
    alias(libs.plugins.kotlinxSerialization)
}

dependencies {
    implementation(project(":shared"))
    implementation(platform(libs.ktor.bom))
    implementation(libs.bundles.ktor.server)
    implementation(libs.slf4j.simple)
}

application {
    mainClass.set("com.gerwalex.retire.server.ServerKt")
}
