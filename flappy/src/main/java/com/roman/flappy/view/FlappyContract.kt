package com.roman.flappy.view

import android.view.MotionEvent
import com.roman.flappy.game.FlappyGame

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
        fun getMainDrawer(): FlappyDrawer

        fun getGame(): FlappyGame
    }

    interface View {
        fun notifyChanged()
        fun setPresenter(presenter: Presenter)
        fun getPresenter(): Presenter?
    }
}
