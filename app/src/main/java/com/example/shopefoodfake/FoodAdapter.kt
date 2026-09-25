package com.example.shopefoodfake

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.shopefoodfake.databinding.ItemFoodBinding

class FoodAdapter(
    private var foods: List<Food>,
    private val onItemClick: (Food) -> Unit
) : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

    inner class FoodViewHolder(private val binding: ItemFoodBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(food: Food) {
            binding.imgFood.setImageResource(food.imageResId)
            binding.tvFoodName.text = food.name
            binding.tvFoodDesc.text = food.description
            binding.tvFoodPrice.text = Food.formatVnd(food.price)

            binding.root.setOnClickListener { onItemClick(food) }
            binding.btnDetail.setOnClickListener { onItemClick(food) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding = ItemFoodBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        holder.bind(foods[position])
    }

    override fun getItemCount(): Int = foods.size

    fun updateData(newFoods: List<Food>) {
        this.foods = newFoods
        notifyDataSetChanged()
    }
}