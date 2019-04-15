package com.demoss.mypaint.util.pagination

sealed class PaginatorViewState<T>
class EMPTY<T> : PaginatorViewState<T>()
class EMPTY_PROGRESS<T> : PaginatorViewState<T>()
class EMPTY_ERROR<T> : PaginatorViewState<T>()
class EMPTY_DATA<T> : PaginatorViewState<T>()
class PAGE_PROGRESS<T> : PaginatorViewState<T>()
class REFRESH<T> : PaginatorViewState<T>()
class RELEASED<T> : PaginatorViewState<T>()
data class DATA<T>(val data: List<T>) : PaginatorViewState<T>()
data class LAST_PAGE<T>(val data: List<T>) : PaginatorViewState<T>()