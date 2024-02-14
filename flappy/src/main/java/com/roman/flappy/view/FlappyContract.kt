package com.roman.flappy.view

import android.view.MotionEvent

/**
 *
 * Author: romanvysotsky
 * Created: 13.02.24
 */

interface FlappyContract {
    interface Presenter {
        fun attachView(view: View)
        fun detachView()

        fun onTouch(event: MotionEvent)

        fun onStart()
        fun onStop()

        fun getMainDrawer(): Drawer
    }

    interface View {
        fun notifyChanged()
        fun setPresenter(presenter: Presenter)
        fun getPresenter(): Presenter?
    }
}
