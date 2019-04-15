package com.demoss.mypaint.data.remote

import com.demoss.mypaint.data.remote.api.apiModule
import com.demoss.mypaint.data.remote.repository.remoteRepositoryModule

val remoteDataModules = listOf(
    apiModule,
    remoteRepositoryModule,
    networkModule
)