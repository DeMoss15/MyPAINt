package com.demoss.mypaint.base.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel<Action, State> : ViewModel() {

    // for view state changing
    protected open val _states: MutableLiveData<State> = MutableLiveData()
    // accessible for view data
    open val states: LiveData<State> get() = _states

    abstract fun executeAction(action: Action)
}