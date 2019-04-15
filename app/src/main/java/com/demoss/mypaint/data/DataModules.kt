package com.demoss.mypaint.data

import com.demoss.mypaint.data.local.localDataModules
import com.demoss.mypaint.data.remote.remoteDataModules
import com.demoss.mypaint.data.repository.repositoryDataModule

val dataModules = localDataModules +
        remoteDataModules +
        repositoryDataModule