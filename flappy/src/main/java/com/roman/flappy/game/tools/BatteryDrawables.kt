package com.roman.flappy.game.tools

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.roman.flappy.R

/**
 *
 * Author: romanvysotsky
 * Created: 26.02.24
 */
class BatteryDrawables(context: Context) {

    private val critical =
        ContextCompat.getDrawable(context, R.drawable.ic_battery_0)

    private val batteries = arrayOf(
        ContextCompat.getDrawable(context, R.drawable.ic_battery_1),
        ContextCompat.getDrawable(context, R.drawable.ic_battery_2),
        ContextCompat.getDrawable(context, R.drawable.ic_battery_3),
        ContextCompat.getDrawable(context, R.drawable.ic_battery_4),
    )

    fun getBatteryDrawable(chargePercentage: Int): Drawable? {
        if (isCritical(chargePercentage))
            return critical

        val interval = 100 / batteries.size
        val result = (chargePercentage / interval)
            .coerceAtLeast(0)
            .coerceAtMost(batteries.size - 1)

        return batteries[result]
    }

    fun isCritical(chargePercentage: Int): Boolean {
        return chargePercentage <= 3
    }

}
