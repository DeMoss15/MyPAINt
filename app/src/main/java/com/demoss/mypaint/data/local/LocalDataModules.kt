package com.demoss.mypaint.data.local

import com.demoss.mypaint.data.local.repository.localRepositoryModule

val localDataModules = listOf(
    dbModule,
    localRepositoryModule
)