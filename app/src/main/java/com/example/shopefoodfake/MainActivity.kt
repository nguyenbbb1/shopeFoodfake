package com.example.shopefoodfake

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopefoodfake.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: FoodAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindowInsets()
        setupRecyclerView()
        setupListeners()
        updateHeaderInfo()
    }

    override fun onResume() {
        super.onResume()
        updateHeaderInfo()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.headerLayout) { view, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            view.updatePadding(top = statusBarHeight)
            insets
        }
    }

    private fun setupRecyclerView() {
        val foods = Food.getMockData()
        adapter = FoodAdapter(foods) { selectedFood ->
            navigateToDetail(selectedFood)
        }
        binding.rvFoodList.layoutManager = LinearLayoutManager(this)
        binding.rvFoodList.adapter = adapter
    }

    private fun setupListeners() {
        binding.searchBarContainer.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }

        binding.btnCartContainer.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }

        binding.btnOpenWheel.setOnClickListener {
            val intent = Intent(this, LuckyWheelActivity::class.java)
            startActivity(intent)
        }

        binding.tvWalletDisplay.setOnClickListener {
            val intent = Intent(this, WalletActivity::class.java)
            startActivity(intent)
        }
    }

    private fun updateHeaderInfo() {
        // Update Wallet Balance
        binding.tvWalletDisplay.text = "💳 Ví: ${WalletManager.getFormattedBalance()}"

        // Update Cart Badge
        val count = CartManager.getTotalCount()
        if (count > 0) {
            binding.tvCartBadge.text = count.toString()
            binding.tvCartBadge.visibility = View.VISIBLE
        } else {
            binding.tvCartBadge.visibility = View.GONE
        }
    }

    private fun navigateToDetail(food: Food) {
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra("EXTRA_FOOD", food)
        }
        startActivity(intent)
    }
}