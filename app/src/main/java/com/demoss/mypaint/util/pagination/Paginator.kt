package com.demoss.mypaint.util.pagination

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*

class Paginator<T>(
    private val scope: CoroutineScope,
    private val requestFabric: suspend (Int) -> List<T>
) {

    companion object {
        const val FIRST_PAGE = 0
    }

    val viewStatesLiveData: LiveData<PaginatorViewState<T>>
        get() = _viewStateLiveData
    private val _viewStateLiveData: MutableLiveData<PaginatorViewState<T>> = MutableLiveData()
    private val currentData: MutableList<T> = mutableListOf()
    private var currentState: LoaderState<T> = LOADER_EMPTY()
        set(value) {
            _viewStateLiveData.postValue(
                when (value) {
                    is LOADER_EMPTY -> EMPTY()
                    is LOADER_EMPTY_DATA -> EMPTY_DATA()
                    is LOADER_EMPTY_ERROR -> EMPTY_ERROR()
                    is LOADER_EMPTY_PROGRESS -> EMPTY_PROGRESS()
                    is LOADER_DATA -> DATA(currentData.toMutableList())
                    is LOADER_ALL_DATA -> LAST_PAGE(currentData)
                    is LOADER_PAGE_PROGRESS -> PAGE_PROGRESS()
                    is LOADER_REFRESH -> REFRESH()
                    is LOADER_RELEASED -> RELEASED()
                    else -> throw RuntimeException("undefined loader class")
                }
            )
            field = value
        }
    private var currentPage: Int = 0

    fun restart() {
        currentState.restart()
    }

    fun refresh() {
        currentState.refresh()
    }

    fun loadNewPage() {
        currentState.loadNewPage()
    }

    fun release() {
        currentState.release()
    }

    // For usage in LoaderStates
    private fun loadPageNumber(pageNumber: Int) {
        scope.launch(CoroutineExceptionHandler { _, exception ->
            exception.printStackTrace()
            currentState.fail(exception)
        }) {
            currentState.newData(requestFabric(pageNumber))
        }
    }

    private interface LoaderState<T> {
        fun restart() {}
        fun refresh() {}
        fun loadNewPage() {}
        fun release() {}
        fun newData(data: List<T>) {}
        fun fail(error: Throwable) {}
    }

    private inner class LOADER_EMPTY : LoaderState<T> {

        override fun refresh() {
            currentState = LOADER_EMPTY_PROGRESS()
            loadPageNumber(FIRST_PAGE)
        }

        override fun release() {
            currentState = LOADER_RELEASED()
        }
    }

    private inner class LOADER_EMPTY_PROGRESS : LoaderState<T> {

        override fun restart() {
            loadPageNumber(FIRST_PAGE)
        }

        override fun newData(data: List<T>) {
            currentData.apply {
                clear()
                addAll(data)
            }
            currentPage = FIRST_PAGE

            currentState = if (data.isNotEmpty()) LOADER_DATA()
            else LOADER_EMPTY_DATA()
        }

        override fun fail(error: Throwable) {
            currentState = LOADER_EMPTY_ERROR()
        }

        override fun release() {
            currentState = LOADER_RELEASED()
        }
    }

    private inner class LOADER_EMPTY_ERROR : LoaderState<T> {

        override fun restart() {
            currentState = LOADER_EMPTY_PROGRESS()
            loadPageNumber(FIRST_PAGE)
        }

        override fun refresh() {
            currentState = LOADER_EMPTY_PROGRESS()
            loadPageNumber(FIRST_PAGE)
        }

        override fun release() {
            currentState = LOADER_RELEASED()
        }
    }

    private inner class LOADER_EMPTY_DATA : LoaderState<T> {

        override fun restart() {
            currentState = LOADER_EMPTY_PROGRESS()
            loadPageNumber(FIRST_PAGE)
        }

        override fun refresh() {
            currentState = LOADER_EMPTY_PROGRESS()
            loadPageNumber(FIRST_PAGE)
        }

        override fun release() {
            currentState = LOADER_RELEASED()
        }
    }

    private inner class LOADER_DATA : LoaderState<T> {

        override fun restart() {
            currentState = LOADER_EMPTY_PROGRESS()
            loadPageNumber(FIRST_PAGE)
        }

        override fun refresh() {
            currentState = LOADER_REFRESH()
            loadPageNumber(FIRST_PAGE)
        }

        override fun loadNewPage() {
            currentState = LOADER_PAGE_PROGRESS()
            loadPageNumber(++currentPage)
        }

        override fun release() {
            currentState = LOADER_RELEASED()
        }
    }

    private inner class LOADER_REFRESH : LoaderState<T> {

        override fun restart() {
            currentState = LOADER_EMPTY_PROGRESS()
            loadPageNumber(FIRST_PAGE)
        }

        override fun newData(data: List<T>) {
            if (data.isNotEmpty()) {
                currentData.apply {
                    clear()
                    addAll(data)
                }
                currentPage = FIRST_PAGE
                currentState = LOADER_DATA()
            } else {
                currentData.clear()
                currentState = LOADER_EMPTY_DATA()
            }
        }

        override fun fail(error: Throwable) {
            currentState = LOADER_DATA()
        }

        override fun release() {
            currentState = LOADER_RELEASED()
        }
    }

    private inner class LOADER_PAGE_PROGRESS : LoaderState<T> {

        override fun restart() {
            currentState = LOADER_EMPTY_PROGRESS()
            loadPageNumber(FIRST_PAGE)
        }

        override fun newData(data: List<T>) {
            currentData.addAll(data)
            currentState = if (data.isNotEmpty()) LOADER_DATA()
            else LOADER_ALL_DATA()
        }

        override fun refresh() {
            currentState = LOADER_REFRESH()
            loadPageNumber(FIRST_PAGE)
        }

        override fun fail(error: Throwable) {
            currentState = LOADER_DATA()
        }

        override fun release() {
            currentState = LOADER_RELEASED()
        }
    }

    private inner class LOADER_ALL_DATA : LoaderState<T> {

        override fun restart() {
            currentState = LOADER_EMPTY_PROGRESS()
            loadPageNumber(FIRST_PAGE)
        }

        override fun refresh() {
            currentState = LOADER_REFRESH()
            loadPageNumber(FIRST_PAGE)
        }

        override fun release() {
            currentState = LOADER_RELEASED()
        }
    }

    private inner class LOADER_RELEASED : LoaderState<T> {
        init {
            if (scope.isActive) scope.cancel()
        }
    }
}
