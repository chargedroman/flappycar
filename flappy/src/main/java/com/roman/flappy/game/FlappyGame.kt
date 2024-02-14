package com.roman.flappy.game

import android.content.Context
import android.graphics.Canvas
import android.view.MotionEvent
import com.roman.flappy.view.BackgroundDrawer
import com.roman.flappy.view.CarDrawer
import com.roman.flappy.view.Drawer

/**
 *
 * Author: romanvysotsky
 * Created: 13.02.24
 */

class FlappyGame(
    private val applicationContext: Context,
    private val triggerRedraw: () -> Unit
): Drawer {

    //ticks 60 times each second
    private val ticker = FlappyTicker(this::tickTock)

    //generally the amount of pixels moved
    private var currentGameSpeed = 20

    //define all the drawers and then call them in the right order in [onDraw]
    private val backgroundDrawer = BackgroundDrawer(applicationContext)
    private val carDrawer = CarDrawer(applicationContext)


    fun start() = synchronized(this) {
        ticker.start()
    }

    fun stop() = synchronized(this) {
        ticker.stop()
    }


    fun onTouch(event: MotionEvent) {
        carDrawer.onTouch(event)

        val action = event.action and MotionEvent.ACTION_MASK
        //println("okhttp $action ${event.x} ${event.y}")
    }

    override fun onDraw(canvas: Canvas) {
        backgroundDrawer.onDraw(canvas)
        carDrawer.onDraw(canvas)
    }


    private fun tickTock() {
        backgroundDrawer.tickTock(currentGameSpeed)
        triggerRedraw.invoke()
    }

}
