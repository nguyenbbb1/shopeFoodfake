package com.example.shopefoodfake

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopefoodfake.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var adapter: FoodAdapter
    private val allFoods = Food.getMockData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindowInsets()
        setupRecyclerView()
        setupListeners()
        filterList("") // Initial display
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.searchHeader) { view, insets ->
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
        adapter = FoodAdapter(emptyList()) { selectedFood ->
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra("EXTRA_FOOD", selectedFood)
            }
            startActivity(intent)
        }
        binding.rvSearchResults.layoutManager = LinearLayoutManager(this)
        binding.rvSearchResults.adapter = adapter
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.etSearchQuery.addTextChangedListener { editable ->
            val query = editable?.toString().orEmpty()
            filterList(query)
        }
    }

    private fun filterList(query: String) {
        val trimmedQuery = query.trim().lowercase()
        val filteredFoods = if (trimmedQuery.isEmpty()) {
            allFoods
        } else {
            allFoods.filter { food ->
                food.name.lowercase().contains(trimmedQuery)
            }
        }

        adapter.updateData(filteredFoods)

        if (filteredFoods.isEmpty()) {
            binding.rvSearchResults.visibility = View.GONE
            binding.tvEmptyState.visibility = View.VISIBLE
        } else {
            binding.rvSearchResults.visibility = View.VISIBLE
            binding.tvEmptyState.visibility = View.GONE
        }
    }
}