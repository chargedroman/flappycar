package com.roman.flappy.game

import android.content.Context
import android.graphics.Canvas
import android.view.MotionEvent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.roman.flappy.game.drawers.BackgroundDrawer
import com.roman.flappy.game.drawers.CarDrawer
import com.roman.flappy.game.models.FlappyGameArgs
import com.roman.flappy.game.models.FlappyGameControl
import com.roman.flappy.game.models.FlappyGameScore
import com.roman.flappy.view.Drawer
import kotlin.math.round

/**
 *
 * Author: romanvysotsky
 * Created: 13.02.24
 */

class FlappyGameImpl(
    applicationContext: Context,
    private val triggerRedraw: () -> Unit
): FlappyGame, Drawer {

    //ticks 60 times each second
    private val ticker = FlappyTicker(this::tickTock)

    //listens to phone sensor
    private val accelerometer = FlappyTilt(applicationContext, this::onTilt)


    //game parameters like initial drive speed & control via touch or sensor
    private var args: FlappyGameArgs? = null
    private var currentTick: Long = 0
    private var currentKmPerH = 0
    private var currentDistanceCm: Long = 0

    //current score like distance ran
    private var gameScoreCurrent: FlappyGameScore? = null
    private val gameScore = MutableLiveData<FlappyGameScore>()


    //define all the drawers and then call them in the right order in [onDraw]
    private val backgroundDrawer = BackgroundDrawer(applicationContext)
    private val carDrawer = CarDrawer(applicationContext)


    override fun initGame(args: FlappyGameArgs) = synchronized(this) {
        this.args = args
        this.currentKmPerH = 0
        this.currentTick = 0
        this.currentDistanceCm = 0
        gameScore.postValue(FlappyGameScore(0, 0))
    }

    override fun startGame() = synchronized(this) {
        val args = args ?: return

        ticker.start()
        if (args.gameControl == FlappyGameControl.SENSOR)
            accelerometer.start()
    }

    override fun stopGame() = synchronized(this) {
        ticker.stop()
        accelerometer.stop()
    }

    override fun getScore(): LiveData<FlappyGameScore> {
        return gameScore
    }


    fun onTouch(event: MotionEvent) {
        if (args?.gameControl == FlappyGameControl.TOUCH)
            carDrawer.onTouch(event)
    }

    private fun onTilt(x: Float, y: Float) {
        if (args?.gameControl == FlappyGameControl.SENSOR)
            carDrawer.onTilt(x, y)
    }


    override fun onDraw(canvas: Canvas) {
        backgroundDrawer.onDraw(canvas)
        carDrawer.onDraw(canvas)
    }


    private fun tickTock() {
        updateGameScore()

        backgroundDrawer.tickTock(currentKmPerH)
        triggerRedraw.invoke()
    }

    private fun updateGameScore() = synchronized(this) {
        val args = args ?: return

        if (currentKmPerH < args.gameSpeedInitialKmPerH)
            currentKmPerH = args.gameSpeedInitialKmPerH

        if (currentKmPerH > args.gameSpeedMaxKmPerH)
            currentKmPerH = args.gameSpeedMaxKmPerH

        currentTick++
        currentDistanceCm += centimetersTravelledInOneTickFor(currentKmPerH)
        val currentDistanceMeters = currentDistanceCm / 100

        val previousScore = gameScoreCurrent
        val newScore = FlappyGameScore(currentDistanceMeters, currentKmPerH)
        if (previousScore != newScore) {
            gameScoreCurrent = newScore
            gameScore.postValue(newScore)
        }

        currentKmPerH += args.gameSpeedController.onTick(currentTick, currentKmPerH)
    }

    private fun centimetersTravelledInOneTickFor(speedKmPerHour: Int): Long {
        val metersPerSecond = (speedKmPerHour * 1000) / 3600.0
        val centimetersPerTick = (metersPerSecond * 100) / 60.0
        return round(centimetersPerTick).toLong()
    }

}
