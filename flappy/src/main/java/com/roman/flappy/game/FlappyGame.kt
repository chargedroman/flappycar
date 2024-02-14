package com.roman.flappy.game

import android.graphics.Canvas
import android.view.MotionEvent
import com.roman.flappy.view.Drawer

/**
 *
 * Author: romanvysotsky
 * Created: 13.02.24
 */

class FlappyGame(private val updateView: () -> Unit): Drawer {

    private val ticker = FlappyTicker(this::tickTock)


    fun start() = synchronized(this) {
        ticker.start()
    }

    fun stop() = synchronized(this) {
        ticker.stop()
    }

    fun onTouch(event: MotionEvent) {

    }

    override fun onDraw(canvas: Canvas) {

    }


    private fun tickTock() {
        updateView.invoke()
    }

}
