package com.demoss.mypaint.domain

import com.demoss.mypaint.domain.model.modelModule
import com.demoss.mypaint.domain.usecase.useCaseModule

val domainModules = listOf(
    useCaseModule,
    modelModule
)