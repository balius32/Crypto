package com.balius.coincap.ui.coinDetail.customChartView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.sin

class CustomChartView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val linePaint = Paint().apply {
        color = Color.GRAY
        strokeWidth = 8f
    }

    private val highlightedLinePaint = Paint().apply {
        color = Color.WHITE
        strokeWidth = 8f
    }

    private val innerCirclePaint = Paint().apply {
        color = Color.WHITE
        strokeWidth = 8f
    }

    private var speed = 0.0

    fun setSpeed(speed: Double) {
        this.speed = speed
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()
        val centerX = width / 2
        val centerY = height / 2
        val outerRadius = (width.coerceAtMost(height) / 2) * 0.9f
        val innerRadius = outerRadius - 40
        val innerCircleRadius = innerRadius - 40

        drawSpeedometerLines(canvas, centerX, centerY, innerRadius, outerRadius)
       drawInnerCircle(canvas, centerX, centerY, innerCircleRadius,speed)
    }

    private fun drawSpeedometerLines(canvas: Canvas, cx: Float, cy: Float, innerRadius: Float, outerRadius: Float) {
        val numLines = 60
        val startAngle = 135
        val endAngle = 45
        val sweepAngle = (360 + endAngle - startAngle) % 360

        val highlightedLines = (speed / 100.0 * numLines).toInt()

        for (i in 0 until numLines) {
            val angle = Math.toRadians(startAngle + sweepAngle * i / numLines.toDouble())
            val startX = cx + innerRadius * cos(angle).toFloat()
            val startY = cy + innerRadius * sin(angle).toFloat()
            val endX = cx + outerRadius * cos(angle).toFloat()
            val endY = cy + outerRadius * sin(angle).toFloat()
            val paint = if (i < highlightedLines) highlightedLinePaint else linePaint
            canvas.drawLine(startX, startY, endX, endY, paint)
        }
    }


   private fun drawInnerCircle(canvas: Canvas, cx: Float, cy: Float, innerRadius: Float, speed: Double) {
       val numPoints = 50
       val startAngle = 135
       val endAngle = 45
       val sweepAngle = (360 + endAngle - startAngle) % 360
       val pointRadius = 4f

       val textPaint = Paint().apply {
           color = Color.CYAN
           textSize = 40f
           textAlign = Paint.Align.CENTER
       }

       val speedTextPaint = Paint().apply {
           color = Color.WHITE
           textSize = 52f
           textAlign = Paint.Align.CENTER
       }

       for (i in 0 until numPoints) {
           val angle = Math.toRadians(startAngle + sweepAngle * i / numPoints.toDouble())
           val x = cx + innerRadius * cos(angle).toFloat()
           val y = cy + innerRadius * sin(angle).toFloat()
           canvas.drawCircle(x, y, pointRadius, innerCirclePaint)
       }


       drawTextAtAngle(canvas, cx, cy, innerRadius - 40, 180, "20", textPaint)
       drawTextAtAngle(canvas, cx, cy, innerRadius - 50, -90, "45", textPaint)
       drawTextAtAngle(canvas, cx, cy, innerRadius - 40, 0, "80", textPaint)


       canvas.drawText(speed.toString(), cx, cy, speedTextPaint)
   }

    private fun drawTextAtAngle(canvas: Canvas, cx: Float, cy: Float, radius: Float, angle: Int, text: String, paint: Paint) {
        val radians = Math.toRadians(angle.toDouble())
        val x = cx + radius * cos(radians).toFloat()
        val y = cy + radius * sin(radians).toFloat()
        canvas.drawText(text, x, y, paint)
    }

}