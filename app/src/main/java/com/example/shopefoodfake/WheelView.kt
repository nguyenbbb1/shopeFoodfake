package com.example.shopefoodfake

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import kotlin.math.min

class WheelView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Only 1 jackpot sector (1 TỶ VNĐ), all other 7 sectors are "Chúc bạn may mắn lần sau"
    val prizes = listOf(
        "🏆 1 TỶ VNĐ",
        "😭 Chúc bạn may mắn lần sau",
        "😭 Chúc bạn may mắn lần sau",
        "😭 Chúc bạn may mắn lần sau",
        "😭 Chúc bạn may mắn lần sau",
        "😭 Chúc bạn may mắn lần sau",
        "😭 Chúc bạn may mắn lần sau",
        "😭 Chúc bạn may mắn lần sau"
    )

    private val sectorColors = listOf(
        Color.parseColor("#E31E24"), // 1 Tỷ - Gold/Red Highlight
        Color.parseColor("#424242"),
        Color.parseColor("#616161"),
        Color.parseColor("#424242"),
        Color.parseColor("#616161"),
        Color.parseColor("#424242"),
        Color.parseColor("#616161"),
        Color.parseColor("#424242")
    )

    private val piePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 28f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }

    private val pointerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FFD54F")
        style = Paint.Style.FILL
    }

    private var currentAngle = 0f
    private var isSpinning = false

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val size = min(width, height)
        val radius = size / 2f * 0.85f
        val centerX = width / 2f
        val centerY = height / 2f
        val rect = RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius)

        val sweepAngle = 360f / prizes.size

        canvas.save()
        canvas.rotate(currentAngle, centerX, centerY)

        for (i in prizes.indices) {
            piePaint.color = sectorColors[i]
            val startAngle = i * sweepAngle
            canvas.drawArc(rect, startAngle, sweepAngle, true, piePaint)

            // Draw prize text
            canvas.save()
            val textAngle = startAngle + sweepAngle / 2f
            canvas.rotate(textAngle, centerX, centerY)
            canvas.drawText(prizes[i], centerX + radius * 0.55f, centerY + 10f, textPaint)
            canvas.restore()
        }

        canvas.restore()

        // Draw Center Pin
        piePaint.color = Color.parseColor("#FFE082")
        canvas.drawCircle(centerX, centerY, radius * 0.18f, piePaint)
        piePaint.color = Color.parseColor("#FF6F00")
        canvas.drawCircle(centerX, centerY, radius * 0.12f, piePaint)

        // Draw Top Indicator Arrow (Pointer)
        val path = Path().apply {
            moveTo(centerX, centerY - radius - 20f)
            lineTo(centerX - 24f, centerY - radius + 30f)
            lineTo(centerX + 24f, centerY - radius + 30f)
            close()
        }
        canvas.drawPath(path, pointerPaint)
    }

    fun spinToSector(targetSectorIndex: Int, onEnd: (Int) -> Unit) {
        if (isSpinning) return
        isSpinning = true

        val sectorSweep = 360f / prizes.size
        // Sector 0 center is at 0 + sweep/2. Top pointer is at 270 degrees.
        val targetSectorCenterAngle = targetSectorIndex * sectorSweep + sectorSweep / 2f
        val destinationAngle = 270f - targetSectorCenterAngle
        val extraRounds = 5 * 360f // 5 full spins
        val targetTotalRotation = currentAngle + extraRounds + (destinationAngle - (currentAngle % 360f) + 360f) % 360f

        val animator = ValueAnimator.ofFloat(currentAngle, targetTotalRotation).apply {
            duration = 4500
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { anim ->
                currentAngle = anim.animatedValue as Float
                invalidate()
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    isSpinning = false
                    onEnd(targetSectorIndex)
                }
            })
        }
        animator.start()
    }
}