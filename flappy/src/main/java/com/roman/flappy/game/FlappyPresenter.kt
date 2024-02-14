package com.roman.flappy.game

import android.view.MotionEvent
import com.roman.flappy.view.Drawer
import com.roman.flappy.view.FlappyContract

/**
 *
 * Author: romanvysotsky
 * Created: 13.02.24
 */

class FlappyPresenter : FlappyContract.Presenter {

    private var view: FlappyContract.View? = null

    private val flappyGame = FlappyGame {
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

    override fun onStart() {
        flappyGame.start()
    }

    override fun onStop() {
        flappyGame.stop()
    }


    override fun getMainDrawer(): Drawer {
        return flappyGame
    }


}
