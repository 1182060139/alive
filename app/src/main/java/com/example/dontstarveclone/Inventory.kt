package com.example.dontstarveclone

class Inventory {
    data class Slot(val name: String, var count: Int)
    val items = mutableListOf<Slot>()
    
    fun addItem(name: String, count: Int = 1) {
        val exist = items.find { it.name == name }
        if (exist != null) exist.count += count
        else items.add(Slot(name, count))
    }
    
    fun removeItem(name: String, count: Int = 1): Boolean {
        val exist = items.find { it.name == name } ?: return false
        if (exist.count < count) return false
        exist.count -= count
        if (exist.count <= 0) items.remove(exist)
        return true
    }
    
    fun hasItem(name: String, count: Int = 1): Boolean {
        val exist = items.find { it.name == name } ?: return false
        return exist.count >= count
    }
    
    fun update(delta: Float) { }
}