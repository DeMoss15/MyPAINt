package com.demoss.mypaint.base.mvvm

sealed class PaginatorAction

object PAGINATOR_RESTART: PaginatorAction()
object PAGINATOR_REFRESH: PaginatorAction()
object PAGINATOR_LOAD_NEXT_PAGE: PaginatorAction()