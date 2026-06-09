package com.example.dontstarveclone

import android.graphics.*
import android.view.MotionEvent

class GameUI {
    // 按钮区域定义
    data class Button(val x: Float, val y: Float, val w: Float, val h: Float, val label: String)
    
    private val collectBtn = Button(30f, 600f, 120f, 60f, "采集")
    private val craftBtn = Button(160f, 600f, 120f, 60f, "合成")
    private var showCraftMenu = false
    
    fun isOverControl(tx: Float, ty: Float, view: GameSurfaceView): Boolean {
        // 虚拟摇杆区域（左下）和按钮区域
        if (tx < 200 && ty > view.screenH - 200) return true
        if (tx > view.screenW - 200 && ty > view.screenH - 200) return true
        return isButtonHit(collectBtn, tx, ty) || isButtonHit(craftBtn, tx, ty)
    }
    
    private fun isButtonHit(btn: Button, tx: Float, ty: Float): Boolean {
        return tx > btn.x && tx < btn.x + btn.w && ty > btn.y && ty < btn.y + btn.h
    }
    
    fun onTouch(event: MotionEvent, view: GameSurfaceView): Boolean {
        if (event.action != MotionEvent.ACTION_DOWN) return false
        
        if (isButtonHit(collectBtn, event.x, event.y)) {
            view.world.collectNearest(view.player, view)
            return true
        }
        if (isButtonHit(craftBtn, event.x, event.y)) {
            showCraftMenu = !showCraftMenu
            return true
        }
        // 合成菜单中的合成按钮处理
        if (showCraftMenu) {
            // 简化：直接尝试合成第一个配方
            Crafting.craft(Crafting.recipes.first(), view.inventory)
            showCraftMenu = false
            return true
        }
        return false
    }
    
    fun draw(canvas: Canvas, paint: Paint, view: GameSurfaceView) {
        paint.style = Paint.Style.FILL
        
        // 采集按钮
        drawButton(canvas, paint, collectBtn)
        
        // 合成按钮
        drawButton(canvas, paint, craftBtn)
        
        // 背包显示
        paint.textSize = 24f
        paint.color = Color.WHITE
        var yPos = 80f
        for (item in view.inventory.items) {
            canvas.drawText("${item.name} x${item.count}", 20f, yPos, paint)
            yPos += 30
        }
        
        // 状态条
        paint.color = Color.RED
        canvas.drawRect(20f, 20f, 20f + view.player.health, 35f, paint)
        paint.color = Color.YELLOW
        canvas.drawRect(20f, 38f, 20f + view.player.hunger, 53f, paint)
        paint.color = Color.MAGENTA
        canvas.drawRect(20f, 56f, 20f + view.player.sanity, 71f, paint)
    }
    
    private fun drawButton(canvas: Canvas, paint: Paint, btn: Button) {
        paint.color = Color.argb(180, 50, 50, 50)
        canvas.drawRoundRect(btn.x, btn.y, btn.x + btn.w, btn.y + btn.h, 10f, 10f, paint)
        paint.color = Color.WHITE
        paint.textSize = 30f
        canvas.drawText(btn.label, btn.x + 20, btn.y + 40, paint)
    }
}