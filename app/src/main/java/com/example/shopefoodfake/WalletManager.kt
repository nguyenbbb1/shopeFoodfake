package com.example.shopefoodfake

object WalletManager {
    // Initial personal wallet balance: 500.000 VNĐ
    var balance: Long = 500000L
        private set

    fun addBalance(amount: Long) {
        if (amount > 0) {
            balance += amount
        }
    }

    fun deductBalance(amount: Long): Boolean {
        if (amount in 1..balance) {
            balance -= amount
            return true
        }
        return false
    }

    fun resetBalance() {
        balance = 0L
    }

    fun getFormattedBalance(): String {
        return Food.formatVnd(balance)
    }
}