package com.roman.flappy.game.drawers

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.TypedValue
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.roman.flappy.R
import com.roman.flappy.game.models.FlappyGameScore
import com.roman.flappy.game.tools.BatteryDrawables
import com.roman.flappy.game.tools.FlappyBatteryController
import com.roman.flappy.game.tools.FlappyGameSpeedController
import com.roman.flappy.view.FlappyDrawer

/**
 *
 * Author: romanvysotsky
 * Created: 23.02.24
 */

class FlappyDisplayDrawer(
    context: Context,
    private val speedController: FlappyGameSpeedController,
    private val batteryController: FlappyBatteryController
): FlappyDrawer {

    private val gameScoreCurrent: FlappyGameScore =
        FlappyGameScore(0, 0, null)

    fun getCurrentSpeed() = speedController.getCurrentSpeedKmPerHour()
    fun getCurrentSpeedPercent() = speedController.getCurrentSpeedPercent()
    fun getGameScore() = gameScoreCurrent


    private val backgroundPaint = Paint().apply {
        color = context.getColor(android.R.color.darker_gray)
        style = Paint.Style.FILL
        alpha = 230 //90%
    }

    private val textPaint = Paint().apply {
        val textSizeInSp = 16 //sp
        val scaledPixels = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, textSizeInSp.toFloat(), context.resources.displayMetrics
        )
        textSize = scaledPixels
        color = context.getColor(android.R.color.black)
        typeface = ResourcesCompat.getFont(context, R.font.quicksand_bold)
    }

    private val speedometerDrawable = ContextCompat.getDrawable(context, R.drawable.ic_speedometer)
    private val distanceDrawable = ContextCompat.getDrawable(context, R.drawable.ic_road)
    private val batteryDrawables = BatteryDrawables(context)
    private var batteryCriticallyBlinking = 0
    private val iconBounds = Rect()


    override fun onDraw(canvas: Canvas) {
        val iconSize = canvas.width / 14
        val iconMargin = iconSize / 4
        drawBackground(canvas, iconSize, iconMargin)
        drawSpeedometer(canvas, iconSize, iconMargin)
        drawDistanceTravelled(canvas, iconSize, iconMargin)
        drawBattery(canvas, iconSize, iconMargin)
    }

    private fun drawBackground(canvas: Canvas, iconSize: Int, iconMargin: Int) {
        iconBounds.top = canvas.height - iconSize - iconMargin * 2
        iconBounds.bottom = canvas.height
        iconBounds.left = 0
        iconBounds.right = canvas.width

        canvas.drawRect(iconBounds, backgroundPaint)
    }

    private fun drawSpeedometer(canvas: Canvas, iconSize: Int, iconMargin: Int) {
        iconBounds.left = iconMargin
        iconBounds.right = iconSize + iconMargin
        iconBounds.top = canvas.height - (iconSize + iconMargin)
        iconBounds.bottom = canvas.height - iconMargin
        speedometerDrawable?.bounds = iconBounds
        speedometerDrawable?.draw(canvas)

        val text = gameScoreCurrent.currentSpeedKmPerHour.toString() + " km/h"
        val textX = iconBounds.right.toFloat() + iconMargin / 2
        val textY = iconBounds.top.toFloat() + iconSize / 2.2f + iconMargin
        canvas.drawText(text, textX, textY, textPaint)
    }

    private fun drawDistanceTravelled(canvas: Canvas, iconSize: Int, iconMargin: Int) {
        val additionalMargin = iconSize * 4
        iconBounds.left = additionalMargin + iconMargin
        iconBounds.right = additionalMargin + iconSize + iconMargin
        iconBounds.top = canvas.height - (iconSize + iconMargin)
        iconBounds.bottom = canvas.height - iconMargin
        distanceDrawable?.bounds = iconBounds
        distanceDrawable?.draw(canvas)

        val text = gameScoreCurrent.distanceMeters.toString() + "m"
        val textX = iconBounds.right.toFloat() + iconMargin / 2
        val textY = iconBounds.top.toFloat() + iconSize / 2.2f + iconMargin
        canvas.drawText(text, textX, textY, textPaint)
    }

    private fun drawBattery(canvas: Canvas, iconSize: Int, iconMargin: Int) {
        val status = gameScoreCurrent.batteryStatus ?: return
        val percentage = status.getPercentage()
        val isCritical = batteryDrawables.isCritical(percentage)

        val batteryDrawable = batteryDrawables.getBatteryDrawable(percentage)
        iconBounds.left = canvas.width - iconSize - iconMargin
        iconBounds.right = canvas.width - iconMargin
        iconBounds.top = canvas.height - (iconSize + iconMargin)
        iconBounds.bottom = canvas.height - iconMargin
        batteryDrawable?.bounds = iconBounds
        batteryDrawable?.draw(canvas)

        val text = "$percentage%"
        val textX = iconBounds.left.toFloat() - iconSize
        val textY = iconBounds.top.toFloat() + iconSize / 2.2f + iconMargin
        canvas.drawText(text, textX, textY, textPaint)

        if (canDrawBigBlinkingBattery(isCritical)) {
            val iconSizeHalf = canvas.width / 6
            val centerWidth = canvas.width / 2
            val centerHeight = canvas.height / 2
            iconBounds.left = centerWidth - iconSizeHalf
            iconBounds.right = centerWidth + iconSizeHalf
            iconBounds.top = centerHeight - iconSizeHalf
            iconBounds.bottom = centerHeight + iconSizeHalf
            batteryDrawable?.bounds = iconBounds
            batteryDrawable?.draw(canvas)
        }
    }

    /**
     * to create a blinking effect for when battery is critical
     */
    private fun canDrawBigBlinkingBattery(isBatteryCritical: Boolean): Boolean {
        return if (isBatteryCritical.not())
            false
        else {
            batteryCriticallyBlinking++
            //fix overflow
            if (batteryCriticallyBlinking < 1)
                batteryCriticallyBlinking = 0

            batteryCriticallyBlinking % 60 < 30
        }
    }


    fun tickTock(currentTick: Long, currentKmPerH: Int, isOnLane: Boolean, isOnCone: Boolean) {
        speedController.onTick(currentTick, batteryController.getCurrentBatteryStatus())
        batteryController.onTick(currentTick, currentKmPerH)

        gameScoreCurrent.batteryStatus = batteryController.getCurrentBatteryStatus()
        gameScoreCurrent.currentSpeedKmPerHour = currentKmPerH
        gameScoreCurrent.distanceMeters = speedController.getCurrentDistanceMeters()

        batteryController.notifyCarOnChargingLane(isOnLane)
        batteryController.notifyCarOnCone(isOnCone)
    }


}
