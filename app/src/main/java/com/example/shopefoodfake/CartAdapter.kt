package com.example.shopefoodfake

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.shopefoodfake.databinding.ItemCartBinding

class CartAdapter(
    private var cartItems: List<CartItem>,
    private val onQuantityChanged: (CartItem, Int) -> Unit,
    private val onDeleteClicked: (CartItem) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(private val binding: ItemCartBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CartItem) {
            binding.imgCartFood.setImageResource(item.food.imageResId)
            binding.tvCartFoodName.text = item.food.name
            binding.tvCartFoodPrice.text = Food.formatVnd(item.food.price)
            binding.tvCartQuantity.text = item.quantity.toString()

            binding.btnCartPlus.setOnClickListener {
                onQuantityChanged(item, item.quantity + 1)
            }

            binding.btnCartMinus.setOnClickListener {
                if (item.quantity > 1) {
                    onQuantityChanged(item, item.quantity - 1)
                } else {
                    onDeleteClicked(item)
                }
            }

            binding.btnDelete.setOnClickListener {
                onDeleteClicked(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(cartItems[position])
    }

    override fun getItemCount(): Int = cartItems.size

    fun updateData(newItems: List<CartItem>) {
        this.cartItems = newItems
        notifyDataSetChanged()
    }
}