package com.demoss.mypaint.domain.usecase.base

interface BaseUseCase<T, Params> {
    suspend fun execute(params: Params): T
}