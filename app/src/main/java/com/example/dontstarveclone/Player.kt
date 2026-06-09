package com.example.dontstarveclone

import android.graphics.*
import kotlin.math.*

class Player {
    var x = 0f
    var y = 0f
    var targetX = -1f
    var targetY = -1f
    val speed = 120f
    
    var health = 100f
    var hunger = 100f
    var sanity = 100f
    
    private var walkAnimFrame = 0f
    private val walkSpeed = 5f
    var facingRight = true

    fun update(delta: Float, view: GameSurfaceView, touching: Boolean, touchX: Float, touchY: Float) {
        if (view.screenW <= 0f || view.screenH <= 0f) return

        if (touching && !view.ui.isOverControl(touchX, touchY, view)) {
            targetX = touchX
            targetY = touchY
        }
        
        if (targetX >= 0 && targetY >= 0) {
            val dx = targetX - x
            val dy = targetY - y
            val dist = sqrt(dx * dx + dy * dy)
            if (dist < 8f) {
                targetX = -1f
                targetY = -1f
            } else {
                x += dx / dist * speed * delta
                y += dy / dist * speed * delta
                walkAnimFrame += walkSpeed * delta
                facingRight = dx > 0
            }
        } else {
            walkAnimFrame = 0f
        }
        
        x = x.coerceIn(20f, view.screenW - 20f)
        y = y.coerceIn(80f, view.screenH - 20f)
        
        hunger -= 2f * delta
        if (hunger <= 0) {
            hunger = 0f
            health -= 5f * delta
        }
        if (health <= 0) health = 0f
    }
    
    fun draw(canvas: Canvas, paint: Paint, bitmap: Bitmap) {
        // 轻微上下浮动模拟呼吸/行走
        val bounce = if (walkAnimFrame > 0) sin(walkAnimFrame * 2) * 2f else 0f
        
        // 水平翻转
        val matrix = Matrix()
        if (!facingRight) {
            matrix.preScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f)
        }
        matrix.postTranslate(x - bitmap.width / 2f, y - bitmap.height / 2f + bounce)
        
        // 绘制阴影
        paint.setShadowLayer(8f, 3f, 3f, Color.argb(120, 0, 0, 0))
        canvas.drawBitmap(bitmap, matrix, paint)
        paint.setShadowLayer(0f, 0f, 0f, 0)
    }
}