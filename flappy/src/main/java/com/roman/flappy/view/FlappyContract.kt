package com.roman.flappy.view

import android.view.MotionEvent
import com.roman.flappy.game.FlappyGame
import com.roman.flappy.game.models.FlappyGameArgs

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

        fun init(args: FlappyGameArgs)
        fun getGame(): FlappyGame?
    }

    interface View {
        fun notifyChanged()
        fun setPresenter(presenter: Presenter)
        fun getPresenter(): Presenter?
    }
}
