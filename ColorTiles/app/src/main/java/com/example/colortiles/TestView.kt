package com.example.colortiles

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.View
import android.widget.Toast

class TestView(ctx: Context): View(ctx) {
    var h = 1000f; var w = 1000f
    var rectHW = 220f;
    var rectMargin = 40f;
    val field: Array<BooleanArray> = Array(4){BooleanArray(4) {false}}
    val paint = Paint()

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        h = (bottom - top).toFloat(); w = (right - left).toFloat()
        paint.color = Color.BLACK
        paint.strokeWidth = 3f;

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.WHITE)

        val lenRow = field.size
        val lenCol = field[0].size
        var fl = true

        for (row in 0 until lenRow) {
            for (col in 0 until lenCol) {
                paint.color = if (field[row][col]) Color.BLUE else Color.RED
                if(!field[row][col]){
                    fl = false
                }
                canvas.drawRect(rectHW * col + rectMargin, rectHW * row + rectMargin, rectHW * col + rectHW, rectHW * row + rectHW, paint )
            }
        }
        if(fl)
            Toast.makeText(context, "ПОБЕДА!!!", Toast.LENGTH_SHORT).show()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val x = event?.x ?: return false
        val y = event?.y ?: return false

        if (event.action == ACTION_DOWN) {
            val lenRow = field.size
            val lenCol = field[0].size

            for (row in 0 until lenRow) {
                for (col in 0 until lenCol) {
                    val left = rectMargin + col * rectHW
                    val top = rectMargin + row * rectHW
                    val right = left + rectHW
                    val bottom = top + rectHW

                    if (x in left..right && y in top..bottom) {
                        Log.i("Нажатие", "Попал в плитку: row=$row, col=$col")

                        for (i in 0 until lenRow) {
                            field[i][col] = !field[i][col]
                        }
                        for (j in 0 until lenCol) {
                            field[row][j] = !field[row][j]
                        }

                        invalidate()
                        return true
                    }
                }
            }
        }

        return true
    }

}