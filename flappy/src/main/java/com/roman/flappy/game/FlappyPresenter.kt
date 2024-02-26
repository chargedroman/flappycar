package com.roman.flappy.game

import android.content.Context
import android.view.MotionEvent
import com.roman.flappy.game.drawers.FlappyLoadingDrawer
import com.roman.flappy.game.models.FlappyGameArgs
import com.roman.flappy.view.FlappyContract
import com.roman.flappy.view.FlappyDrawer

/**
 *
 * Author: romanvysotsky
 * Created: 13.02.24
 */

class FlappyPresenter(private val applicationContext: Context) : FlappyContract.Presenter {

    private val defaultDrawer = FlappyLoadingDrawer(applicationContext)

    private var view: FlappyContract.View? = null

    private var flappyGame: FlappyGameImpl? = null


    override fun attachView(view: FlappyContract.View) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }


    override fun onTouch(event: MotionEvent) {
        flappyGame?.onTouch(event)
    }

    override fun getMainDrawer(): FlappyDrawer {
        return flappyGame ?: defaultDrawer
    }

    override fun init(args: FlappyGameArgs) {
        this.flappyGame = FlappyGameImpl(applicationContext, args) {
            view?.notifyChanged()
        }
    }

    override fun getGame(): FlappyGame? {
        return flappyGame
    }


}
