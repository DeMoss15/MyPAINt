package com.demoss.mypaint.base

import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.demoss.mypaint.base.mvvm.BaseView
import com.demoss.mypaint.base.mvvm.BaseViewModel

abstract class BaseActivity<UserCommand, State, VM : BaseViewModel<UserCommand, State>>
    : AppCompatActivity(), BaseView<UserCommand, State, VM> {

    abstract val layoutResourceId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutResourceId)
        subscribeToViewModel(this)
    }

    protected fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    protected fun hideKeyboard() {
        (getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow((currentFocus ?: View(this)).windowToken, 0)
    }
}