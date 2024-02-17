package com.roman.flappy.game.drawers

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import com.roman.flappy.view.Drawer

/**
 * just draws some simple loading stuff to show until presenter is attached
 *
 * Author: romanvysotsky
 * Created: 13.02.24
 */

class LoadingDrawer(context: Context): Drawer {

    private val fullViewRect = Rect()
    private val paint = Paint()

    init {
        paint.color = context.getColor(android.R.color.darker_gray)
        paint.style = Paint.Style.FILL
    }

    override fun onDraw(canvas: Canvas) {
        fullViewRect.right = canvas.width
        fullViewRect.bottom = canvas.height
        canvas.drawRect(fullViewRect, paint)
    }

}
