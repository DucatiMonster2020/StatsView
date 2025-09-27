package ru.netology.nmedia.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation
import androidx.core.content.withStyledAttributes
import ru.netology.nmedia.R
import ru.netology.nmedia.util.AndroidUtils
import kotlin.math.min
import kotlin.random.Random
import androidx.core.graphics.withRotation

class StatsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : View(context, attrs, defStyleAttr, defStyleRes) {
    private var rotationAngle: Float = 0f
    private var radius = 0F
    private var center = PointF(0F, 0F)
    private var oval = RectF(0F, 0F, 0F, 0F)
    private var lineWidth = AndroidUtils.dp(context, 5F).toFloat()
    private var fontSize = AndroidUtils.dp(context, 40F).toFloat()
    private var colors = emptyList<Int>()


    init {
        context.withStyledAttributes(attrs, R.styleable.StatsView) {
            lineWidth = getDimension(R.styleable.StatsView_lineWidth, lineWidth)
            fontSize = getDimension(R.styleable.StatsView_fontSize, fontSize)
            val resId = getResourceId(R.styleable.StatsView_colors, 0)
            colors = resources.getIntArray(resId).toList()
        }
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = lineWidth
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = fontSize
    }

    var data: List<Float> = emptyList()
        set(value) {
            field = value
            invalidate()
        }
    fun setRotationAngle(angle: Float) {
        rotationAngle = angle
        invalidate()
    }
    private fun calculateProportions(): List<Float> {
        if (data.isEmpty())
            return emptyList()
        val total = data.sum()
        if (total == 0f)
            return List(data.size) { 0f }
        return data.map { it / total }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        radius = min(w, h) / 2F - lineWidth / 2
        center = PointF(w / 2F, h / 2F)
        oval = RectF(
            center.x - radius, center.y - radius,
            center.x + radius, center.y + radius,
        )
    }

    override fun onDraw(canvas: Canvas) {
        if (data.isEmpty()) {
            return
        }
        val proportions = calculateProportions()
        canvas.withRotation(rotationAngle, center.x, center.y) {
            var startFrom = -95F
            for ((index, proportion) in proportions.withIndex()) {
                val angle = 360F * proportion
                paint.color = colors.getOrNull(index) ?: randomColor()
                drawArc(oval, startFrom, angle, false, paint)
                startFrom += angle
            }
            if (proportions.isNotEmpty()) {
                val firstColor = colors.getOrNull(0) ?: randomColor()
                paint.color = firstColor
            }
            val overlapAngle = 5F
            drawArc(oval, -95F, overlapAngle, false, paint)
        }
        canvas.drawText(
            "%.2f%%".format(proportions.sum() * 100),
            center.x,
            center.y + textPaint.textSize / 4,
            textPaint,
        )
    }
    private fun randomColor() = Random.nextInt(0xFF000000.toInt(), 0xFFFFFFFF.toInt())
    class RotationAnimation(private val view: StatsView) : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            val angle = 360f * interpolatedTime
            view.setRotationAngle(angle)
        }
    }
}