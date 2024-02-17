package com.roman.flappy.game

import android.content.Context
import android.view.MotionEvent
import com.roman.flappy.view.FlappyContract
import com.roman.flappy.view.FlappyDrawer

/**
 *
 * Author: romanvysotsky
 * Created: 13.02.24
 */

class FlappyPresenter(applicationContext: Context) : FlappyContract.Presenter {

    private var view: FlappyContract.View? = null

    private val flappyGame = FlappyGameImpl(applicationContext) {
        view?.notifyChanged()
    }


    override fun attachView(view: FlappyContract.View) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }


    override fun onTouch(event: MotionEvent) {
        flappyGame.onTouch(event)
    }

    override fun getMainDrawer(): FlappyDrawer {
        return flappyGame
    }

    override fun getGame(): FlappyGame {
        return flappyGame
    }


}
