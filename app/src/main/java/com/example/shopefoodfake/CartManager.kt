package com.example.shopefoodfake

import java.io.Serializable

data class CartItem(
    val food: Food,
    var quantity: Int
) : Serializable {
    val totalPrice: Long
        get() = food.price * quantity
}

object CartManager {
    private val cartItems = mutableListOf<CartItem>()

    fun getItems(): List<CartItem> = cartItems.toList()

    fun addItem(food: Food, quantity: Int = 1) {
        val existingItem = cartItems.find { it.food.id == food.id }
        if (existingItem != null) {
            existingItem.quantity += quantity
        } else {
            cartItems.add(CartItem(food, quantity))
        }
    }

    fun removeItem(foodId: Int) {
        cartItems.removeAll { it.food.id == foodId }
    }

    fun updateQuantity(foodId: Int, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeItem(foodId)
            return
        }
        val existingItem = cartItems.find { it.food.id == foodId }
        existingItem?.quantity = newQuantity
    }

    fun getTotalFoodPrice(): Long {
        return cartItems.sumOf { it.totalPrice }
    }

    fun getShippingFee(): Long {
        return if (cartItems.isEmpty()) 0L else 15000L
    }

    fun getDiscount(): Long {
        return if (cartItems.isEmpty()) 0L else 10000L
    }

    fun getFinalTotalPrice(): Long {
        if (cartItems.isEmpty()) return 0L
        return getTotalFoodPrice() + getShippingFee() - getDiscount()
    }

    fun getTotalCount(): Int {
        return cartItems.sumOf { it.quantity }
    }

    fun clearCart() {
        cartItems.clear()
    }
}