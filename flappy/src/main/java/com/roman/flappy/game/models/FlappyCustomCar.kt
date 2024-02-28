package com.roman.flappy.game.models

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import androidx.core.content.ContextCompat
import com.roman.flappy.R

/**
 *
 * Author: romanvysotsky
 * Created: 28.02.24
 */
data class FlappyCustomCar(
    val carTintLayer1: Int,
    val carTintLayer2: Int,
) {

    fun getDrawable(context: Context): Drawable? {
        val carLayer1 = ContextCompat.getDrawable(context, R.drawable.car_layer_1)
        val carLayer2 = ContextCompat.getDrawable(context, R.drawable.car_layer_2)
        val carLayer3 = ContextCompat.getDrawable(context, R.drawable.car_layer_3)

        if (carLayer1 == null || carLayer2 == null || carLayer3 == null) {
            return null
        }

        carLayer1.colorFilter = PorterDuffColorFilter(carTintLayer1, PorterDuff.Mode.SRC_IN)
        carLayer2.colorFilter = PorterDuffColorFilter(carTintLayer2, PorterDuff.Mode.SRC_IN)
        val layers = arrayOf(carLayer1, carLayer2, carLayer3)

        return LayerDrawable(layers)
    }

}
