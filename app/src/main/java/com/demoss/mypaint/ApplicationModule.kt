package com.demoss.mypaint

import com.demoss.mypaint.data.dataModules
import com.demoss.mypaint.domain.domainModules
import com.demoss.mypaint.presentation.presentationModules
import org.koin.dsl.module.Module

val applicationModule: List<Module> = presentationModules +
        domainModules +
        dataModules