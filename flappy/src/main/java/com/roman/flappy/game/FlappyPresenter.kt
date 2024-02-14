package com.roman.flappy.game

import android.content.Context
import android.view.MotionEvent
import com.roman.flappy.game.models.FlappyGameArgs
import com.roman.flappy.view.Drawer
import com.roman.flappy.view.FlappyContract

/**
 *
 * Author: romanvysotsky
 * Created: 13.02.24
 */

class FlappyPresenter(private val applicationContext: Context) : FlappyContract.Presenter {

    private var view: FlappyContract.View? = null

    private val flappyGame = FlappyGame(applicationContext) {
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

    override fun onStart(gameArgs: FlappyGameArgs) {
        flappyGame.start(gameArgs)
    }

    override fun onStop() {
        flappyGame.stop()
    }


    override fun getMainDrawer(): Drawer {
        return flappyGame
    }


}
