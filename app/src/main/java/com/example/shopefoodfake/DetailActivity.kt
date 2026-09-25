package com.example.shopefoodfake

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.shopefoodfake.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private var food: Food? = null
    private var quantity: Int = 1
    private var tapCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        food = getFoodFromIntent()

        if (food == null) {
            Toast.makeText(this, "Không tìm thấy thông tin món ăn", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupWindowInsets()
        displayFoodDetails()
        setupListeners()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.imgFoodDetail) { view, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            val density = resources.displayMetrics.density
            val defaultPadding = (24 * density).toInt()
            view.updatePadding(top = statusBarHeight + defaultPadding)
            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.btnBack) { view, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            val density = resources.displayMetrics.density
            val defaultMarginTop = (16 * density).toInt()
            val marginParams = view.layoutParams as? ViewGroup.MarginLayoutParams
            marginParams?.topMargin = statusBarHeight + defaultMarginTop
            view.layoutParams = marginParams
            insets
        }
    }

    @Suppress("DEPRECATION")
    private fun getFoodFromIntent(): Food? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("EXTRA_FOOD", Food::class.java)
        } else {
            intent.getSerializableExtra("EXTRA_FOOD") as? Food
        }
    }

    private fun displayFoodDetails() {
        food?.let { food ->
            binding.imgFoodDetail.setImageResource(food.imageResId)
            binding.tvFoodName.text = food.name
            binding.tvFoodPrice.text = "${Food.formatVnd(food.price)} / phần"
            binding.tvFoodIngredients.text = food.ingredients
            binding.tvFoodDescription.text = food.description

            // Always hide secret code card by default
            binding.cardSecretCode.visibility = View.GONE
            // Set random secret code
            binding.tvSecretCode.text = MinigameManager.secretCode

            updateQuantityAndTotal()
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Tap Image strictly 10 times for Phở Bò to reveal random secret code!
        binding.imgFoodDetail.setOnClickListener {
            food?.let { currentFood ->
                if (currentFood.name.contains("Phở bò", ignoreCase = true)) {
                    tapCount++
                    if (tapCount < 10) {
                        Toast.makeText(
                            this,
                            "🔍 Chạm $tapCount/10 vào Phở Bò...",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (tapCount == 10) {
                        binding.cardSecretCode.visibility = View.VISIBLE
                        Toast.makeText(
                            this,
                            "🎉 Đã đủ 10 lần chạm! Mã bí mật đã xuất hiện bên dưới.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

        binding.btnPlus.setOnClickListener {
            quantity++
            updateQuantityAndTotal()
        }

        binding.btnMinus.setOnClickListener {
            if (quantity > 1) {
                quantity--
                updateQuantityAndTotal()
            }
        }

        binding.btnAddToCart.setOnClickListener {
            food?.let { currentFood ->
                CartManager.addItem(currentFood, quantity)
                Toast.makeText(
                    this,
                    "🛒 Đã thêm $quantity phần ${currentFood.name} vào giỏ hàng!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.btnBuyNow.setOnClickListener {
            food?.let { currentFood ->
                CartManager.addItem(currentFood, quantity)
                val intent = Intent(this, CartActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun updateQuantityAndTotal() {
        food?.let { food ->
            binding.tvQuantity.text = quantity.toString()
            val total = food.price * quantity
            binding.tvTotalPrice.text = Food.formatVnd(total)
        }
    }
}