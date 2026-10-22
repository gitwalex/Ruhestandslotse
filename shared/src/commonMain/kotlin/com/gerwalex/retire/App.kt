package com.gerwalex.retire

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.gerwalex.retire.projection.ProjectionScreenModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val appModule = module {
    // Beispiel für Ihr ProjectionScreenModel
    viewModel { ProjectionScreenModel(get()) }
}

// 2. Zentrale Start-Funktion für Koin (nimmt plattformspezifische Module/Kontexte entgegen)
fun initKoin(appDeclaration: KoinAppDeclaration = {}, extraModule: Module = module {}) {
    startKoin {
        appDeclaration()
        modules(
            appModule,
            extraModule
        )
    }
}


@Composable
@Preview
fun App() {
    MaterialTheme {
        ProjectionScreen()
    }
}