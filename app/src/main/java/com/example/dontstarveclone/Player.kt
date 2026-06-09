package com.example.dontstarveclone

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import kotlin.math.*

class Player {
    var x = 0f
    var y = 0f
    var targetX = -1f
    var targetY = -1f
    val speed = 120f // 每秒像素
    
    var health = 100f
    var hunger = 100f
    var sanity = 100f
    
    private var walkAnimFrame = 0f
    private val walkSpeed = 5f
    private var facingRight = true
    
    fun update(delta: Float, view: GameSurfaceView, touching: Boolean, touchX: Float, touchY: Float) {
        // 防止屏幕尚未初始化导致的崩溃
        if (view.screenW <= 0f || view.screenH <= 0f) return

        // 移动目标由触屏设定，不在虚拟摇杆区域就移动
        if (touching && !view.ui.isOverControl(touchX, touchY, view)) {
            targetX = touchX
            targetY = touchY
        }
        
        // 向目标移动
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
            walkAnimFrame = 0f // 待机
        }
        
        // 边界（确保屏幕有效后计算）
        x = x.coerceIn(20f, view.screenW - 20f)
        y = y.coerceIn(80f, view.screenH - 20f)
        
        // 饥饿下降
        hunger -= 2f * delta
        if (hunger <= 0) {
            hunger = 0f
            health -= 5f * delta
        }
        if (health <= 0) health = 0f
    }
    
    fun draw(canvas: Canvas, paint: Paint) {
        val headRadius = 12f
        val bodyLen = 16f
        val legLen = 14f
        val armLen = 14f
        
        val angle = if (walkAnimFrame > 0) sin(walkAnimFrame * 2) * 0.4f else 0f
        
        paint.strokeWidth = 3f
        paint.style = Paint.Style.FILL_AND_STROKE
        
        // 头部
        paint.color = Color.rgb(255, 220, 180)
        canvas.drawCircle(x, y - bodyLen/2 - headRadius, headRadius, paint)
        
        // 身体
        paint.color = Color.rgb(100, 100, 200)
        canvas.drawLine(x, y - bodyLen/2, x, y + bodyLen/2, paint)
        
        // 腿
        paint.color = Color.rgb(80, 80, 80)
        canvas.drawLine(x, y + bodyLen/2, x - 6 + angle*20, y + bodyLen/2 + legLen, paint)
        canvas.drawLine(x, y + bodyLen/2, x + 6 - angle*20, y + bodyLen/2 + legLen, paint)
        
        // 手臂
        canvas.drawLine(x, y - bodyLen/4, x - 8 + angle*10, y + bodyLen/4, paint)
        canvas.drawLine(x, y - bodyLen/4, x + 8 - angle*10, y + bodyLen/4, paint)
        
        paint.style = Paint.Style.FILL
    }
}