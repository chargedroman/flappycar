package com.roman.flappy.game

import android.content.Context
import android.graphics.Canvas
import android.view.MotionEvent
import com.roman.flappy.game.models.FlappyGameArgs
import com.roman.flappy.game.models.FlappyGameControl
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

    //listens to phone sensor
    private val accelerometer = FlappyTilt(applicationContext, this::onTilt)

    private var args: FlappyGameArgs? = null


    //define all the drawers and then call them in the right order in [onDraw]
    private val backgroundDrawer = BackgroundDrawer(applicationContext)
    private val carDrawer = CarDrawer(applicationContext)


    fun start(args: FlappyGameArgs) = synchronized(this) {
        this.args = args
        ticker.start()

        if (args.gameControl == FlappyGameControl.SENSOR)
            accelerometer.start()
    }

    fun stop() = synchronized(this) {
        this.args = null
        ticker.stop()
        accelerometer.stop()
    }


    fun onTouch(event: MotionEvent) {
        if (args?.gameControl == FlappyGameControl.TOUCH)
            carDrawer.onTouch(event)
    }

    fun onTilt(x: Float, y: Float) {
        if (args?.gameControl == FlappyGameControl.SENSOR)
            carDrawer.onTilt(x, y)
    }


    override fun onDraw(canvas: Canvas) {
        backgroundDrawer.onDraw(canvas)
        carDrawer.onDraw(canvas)
    }


    private fun tickTock() {
        backgroundDrawer.tickTock(args?.gameSpeed ?: 0)
        triggerRedraw.invoke()
    }

}
