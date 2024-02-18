package com.roman.flappy.game

import android.content.Context
import android.graphics.Canvas
import android.view.MotionEvent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.roman.flappy.game.drawers.FlappyBackgroundDrawer
import com.roman.flappy.game.drawers.FlappyCarDrawer
import com.roman.flappy.game.drawers.FlappyChargingLaneDrawer
import com.roman.flappy.game.models.FlappyGameArgs
import com.roman.flappy.game.models.FlappyGameControl
import com.roman.flappy.game.models.FlappyGameScore
import com.roman.flappy.view.FlappyDrawer

/**
 *
 * Author: romanvysotsky
 * Created: 13.02.24
 */

class FlappyGameImpl(
    applicationContext: Context,
    private val triggerRedraw: () -> Unit
): FlappyGame, FlappyDrawer {

    //ticks 60 times each second
    private val ticker = FlappyTicker(this::tickTock)

    //listens to phone sensor
    private val accelerometer = FlappyTilt(applicationContext, this::onTilt)


    //game parameters like initial drive speed & control via touch or sensor
    private var args: FlappyGameArgs? = null
    private var currentTick: Long = 0

    //current score like distance ran
    private val gameScoreCurrent: FlappyGameScore = FlappyGameScore(0, 0, null)
    private val gameScore = MutableLiveData<FlappyGameScore>()


    //define all the drawers and then call them in the right order in [onDraw]
    private val backgroundDrawer = FlappyBackgroundDrawer(applicationContext)
    private val laneDrawer = FlappyChargingLaneDrawer(applicationContext)
    private val carDrawer = FlappyCarDrawer(applicationContext)


    override fun initGame(args: FlappyGameArgs) = synchronized(this) {
        this.args = args
        this.currentTick = 0
        gameScore.postValue(FlappyGameScore(0, 0, null))
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
        if (args?.gameControl == FlappyGameControl.SENSOR) {
            val percent = args?.gameSpeedController?.getCurrentSpeedPercent() ?: 0.0
            carDrawer.onTilt(x, y, percent)
        }
    }


    override fun onDraw(canvas: Canvas) {
        backgroundDrawer.onDraw(canvas)
        laneDrawer.onDraw(canvas)
        carDrawer.onDraw(canvas)
    }


    private fun tickTock() {
        checkIfGameOver()
        onTickUpdateGameScore()
        triggerRedraw.invoke()
    }

    private fun onTickUpdateGameScore() = synchronized(this) {
        val args = args ?: return

        currentTick++

        val currentBatteryStatus = args.gameBatteryController.getCurrentBatteryStatus()
        val currentKmPerH = args.gameSpeedController.getCurrentSpeedKmPerHour()

        args.gameSpeedController.onTick(currentTick, currentBatteryStatus)
        args.gameBatteryController.onTick(currentTick, currentKmPerH)

        gameScoreCurrent.batteryStatus = currentBatteryStatus
        gameScoreCurrent.currentSpeedKmPerHour = currentKmPerH
        gameScoreCurrent.distanceMeters = args.gameSpeedController.getCurrentDistanceMeters()
        gameScore.postValue(gameScoreCurrent)

        val isCarOnChargingLane = laneDrawer.isOnChargingLane(carDrawer.getCarBounds())
        carDrawer.notifyCarOnChargingLane(isCarOnChargingLane)
        args.gameBatteryController.notifyCarOnChargingLane(isCarOnChargingLane)

        backgroundDrawer.tickTock(currentKmPerH)
        laneDrawer.tickTock(currentTick, currentKmPerH)
    }


    private fun checkIfGameOver() {
        if (gameScoreCurrent.isGameOver())
            stopGame()
    }


}
