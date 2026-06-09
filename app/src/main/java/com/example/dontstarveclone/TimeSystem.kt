package com.example.dontstarveclone

class TimeSystem {
    var timeOfDay = 0f // 0-1, 0=黎明, 0.25=正午, 0.75=黄昏, 1=午夜
    private val cycleSpeed = 0.02f // 一个周期约50秒
    
    val isNight: Boolean
        get() = timeOfDay > 0.7f || timeOfDay < 0.2f
    
    fun update(delta: Float) {
        timeOfDay += delta * cycleSpeed
        if (timeOfDay > 1f) timeOfDay -= 1f
    }
}