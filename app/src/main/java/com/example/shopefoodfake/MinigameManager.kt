package com.example.shopefoodfake

import kotlin.random.Random

object MinigameManager {
    const val JACKPOT_PRIZE = 1000000000L // 1 Billion VNĐ

    // Random 6-digit secret code generated on app start
    var secretCode: String = Random.nextInt(100000, 1000000).toString()
        private set

    var isJackpotUnlocked: Boolean = false
    var spinCount: Int = 3

    fun generateNewSecretCode(): String {
        secretCode = Random.nextInt(100000, 1000000).toString()
        return secretCode
    }

    fun unlockJackpot() {
        isJackpotUnlocked = true
    }

    fun consumeJackpot() {
        isJackpotUnlocked = false
    }

    fun addSpin(count: Int = 1) {
        spinCount += count
    }

    fun useSpin(): Boolean {
        if (spinCount > 0) {
            spinCount--
            return true
        }
        return false
    }
}