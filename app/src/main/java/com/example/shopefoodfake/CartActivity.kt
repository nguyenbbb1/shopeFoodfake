package com.example.shopefoodfake

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopefoodfake.databinding.ActivityCartBinding

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    private lateinit var adapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindowInsets()
        setupRecyclerView()
        setupListeners()
        updateCartState()
    }

    override fun onResume() {
        super.onResume()
        updateCartState()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.cartHeader) { view, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            val density = resources.displayMetrics.density
            val defaultPaddingVertical = (12 * density).toInt()
            view.updatePadding(
                top = statusBarHeight + defaultPaddingVertical,
                bottom = defaultPaddingVertical
            )
            insets
        }
    }

    private fun setupRecyclerView() {
        adapter = CartAdapter(
            cartItems = CartManager.getItems(),
            onQuantityChanged = { item, newQty ->
                CartManager.updateQuantity(item.food.id, newQty)
                updateCartState()
            },
            onDeleteClicked = { item ->
                CartManager.removeItem(item.food.id)
                updateCartState()
                Toast.makeText(this, "Đã xóa ${item.food.name} khỏi giỏ hàng", Toast.LENGTH_SHORT).show()
            }
        )
        binding.rvCartItems.layoutManager = LinearLayoutManager(this)
        binding.rvCartItems.adapter = adapter
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnExplore.setOnClickListener {
            finish()
        }

        binding.btnClearCart.setOnClickListener {
            if (CartManager.getItems().isEmpty()) {
                Toast.makeText(this, "Giỏ hàng đã trống", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            AlertDialog.Builder(this)
                .setTitle("Xóa giỏ hàng")
                .setMessage("Bạn có chắc chắn muốn xóa tất cả món ăn trong giỏ hàng?")
                .setPositiveButton("Xóa") { _, _ ->
                    CartManager.clearCart()
                    updateCartState()
                    Toast.makeText(this, "Đã xóa toàn bộ giỏ hàng", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Hủy", null)
                .show()
        }

        binding.btnCheckout.setOnClickListener {
            if (CartManager.getItems().isEmpty()) {
                Toast.makeText(this, "Giỏ hàng trống! Vui lòng chọn món ăn.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(this, CheckoutActivity::class.java)
            startActivity(intent)
        }
    }

    private fun updateCartState() {
        val items = CartManager.getItems()
        adapter.updateData(items)

        if (items.isEmpty()) {
            binding.rvCartItems.visibility = View.GONE
            binding.cardSummary.visibility = View.GONE
            binding.layoutEmptyCart.visibility = View.VISIBLE
        } else {
            binding.rvCartItems.visibility = View.VISIBLE
            binding.cardSummary.visibility = View.VISIBLE
            binding.layoutEmptyCart.visibility = View.GONE

            val subtotal = CartManager.getTotalFoodPrice()
            val shipping = CartManager.getShippingFee()
            val discount = CartManager.getDiscount()
            val finalTotal = CartManager.getFinalTotalPrice()

            binding.tvFoodSubtotal.text = Food.formatVnd(subtotal)
            binding.tvShippingFee.text = Food.formatVnd(shipping)
            binding.tvDiscount.text = "-${Food.formatVnd(discount)}"
            binding.tvCartTotalPrice.text = Food.formatVnd(finalTotal)
        }
    }
}