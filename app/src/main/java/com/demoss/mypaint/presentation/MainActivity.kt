package com.demoss.mypaint.presentation

import android.os.Bundle
import android.util.DisplayMetrics
import com.demoss.mypaint.R
import com.demoss.mypaint.base.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject

class MainActivity: BaseActivity<MainAction, MainState, MainViewModel>() {

    override val layoutResourceId: Int = R.layout.activity_main
    override val viewModel: MainViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
//        pv.init(metrics)
//        wv
    }

    override fun dispatchState(newStatus: MainState) {
        // nothing
    }
}