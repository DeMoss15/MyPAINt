package com.demoss.mypaint.base.mvvm

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer

interface BaseView<UserCommand, State, VM : BaseViewModel<UserCommand, State>> {

    // for sending commands to view model
    val viewModel: VM

    fun dispatchState(newStatus: State)
    fun subscribeToViewModel(lifecycleOwner: LifecycleOwner) {
        viewModel.states.observe(lifecycleOwner, Observer<State> { newState -> dispatchState(newState) })
    }
}