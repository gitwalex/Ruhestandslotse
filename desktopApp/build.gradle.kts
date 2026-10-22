import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

dependencies {
    implementation(project(":shared"))

    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutinesSwing)

    implementation(libs.compose.uiToolingPreview)
}
compose.desktop {
    application {
        mainClass = "MainKt" // Pfad zu Ihrer Main-Funktion

        nativeDistributions {
            // Name der App im Startmenü / auf dem Rechner
            packageName = "Ruhestandslotse"
            packageVersion = "1.0.0"
            vendor = "MeinName"

            // Hier legen Sie die gewünschten Zielformate fest:
            targetFormats(
                TargetFormat.Exe,
                //TargetFormat.Msi
            )

            windows {
                // Spezifische Windows-Einstellungen (z.B. Installer-Icon, Menu-Group)
                // iconFile.set(project.file("ic_launcher.ico"))
                upgradeUuid = "11111111-2222-3333-4443-555555555555" // UUID für Updates beibehalten
            }
        }
    }
}