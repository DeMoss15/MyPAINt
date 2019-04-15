package com.demoss.mypaint.presentation

import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.Module
import org.koin.dsl.module.module

val mainModule = module {
    viewModel { MainViewModel() }
}

val presentationModules = listOf(mainModule)
