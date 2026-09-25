package com.example.shopefoodfake

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.shopefoodfake.databinding.ActivityCheckoutBinding

class CheckoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindowInsets()
        populateOrderSummary()
        displayPriceDetails()
        setupListeners()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.checkoutHeader) { view, insets ->
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

    private fun populateOrderSummary() {
        binding.containerOrderItems.removeAllViews()
        val items = CartManager.getItems()

        for (item in items) {
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = 8
                    bottomMargin = 8
                }
            }

            val tvName = TextView(this).apply {
                text = "${item.food.name} (x${item.quantity})"
                setTextColor(Color.parseColor("#222222"))
                textSize = 14f
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1.0f
                )
            }

            val tvPrice = TextView(this).apply {
                text = Food.formatVnd(item.totalPrice)
                setTextColor(Color.parseColor("#EE4D2D"))
                textSize = 14f
                gravity = Gravity.END
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            row.addView(tvName)
            row.addView(tvPrice)
            binding.containerOrderItems.addView(row)
        }
    }

    private fun displayPriceDetails() {
        val foodSubtotal = CartManager.getTotalFoodPrice()
        val shipping = CartManager.getShippingFee()
        val discount = CartManager.getDiscount()
        val finalTotal = CartManager.getFinalTotalPrice()

        binding.tvCheckoutFoodSubtotal.text = Food.formatVnd(foodSubtotal)
        binding.tvCheckoutShipping.text = Food.formatVnd(shipping)
        binding.tvCheckoutDiscount.text = "-${Food.formatVnd(discount)}"
        binding.tvCheckoutFinalTotal.text = Food.formatVnd(finalTotal)
        binding.tvBottomFinalPrice.text = Food.formatVnd(finalTotal)

        binding.rbWallet.text = "💳 Ví cá nhân (Số dư: ${WalletManager.getFormattedBalance()})"
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnConfirmOrder.setOnClickListener {
            val name = binding.etReceiverName.text?.toString()?.trim().orEmpty()
            val phone = binding.etReceiverPhone.text?.toString()?.trim().orEmpty()
            val address = binding.etDeliveryAddress.text?.toString()?.trim().orEmpty()

            if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin giao hàng", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val finalTotal = CartManager.getFinalTotalPrice()
            val selectedPaymentId = binding.rgPaymentMethods.checkedRadioButtonId

            if (selectedPaymentId == R.id.rbWallet) {
                if (WalletManager.balance < finalTotal) {
                    Toast.makeText(this, "Số dư Ví cá nhân không đủ (${WalletManager.getFormattedBalance()})! Vui lòng nạp thêm tiền.", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                WalletManager.deductBalance(finalTotal)
            }

            // Check if Blindbox item x99 exists in cart!
            val blindboxItem = CartManager.getItems().find {
                it.food.name.contains("Blindbox", ignoreCase = true) || it.food.name.contains("Túi Mù", ignoreCase = true)
            }

            if (blindboxItem != null && blindboxItem.quantity >= 99) {
                showSecretCodeDialog(name, address)
            } else {
                completeOrder(name, address)
            }
        }
    }

    private fun showSecretCodeDialog(name: String, address: String) {
        val etInput = EditText(this).apply {
            hint = "Nhập mã 6 chữ số..."
            inputType = InputType.TYPE_CLASS_NUMBER
            textSize = 18f
            gravity = Gravity.CENTER
            setPadding(32, 24, 32, 24)
        }

        AlertDialog.Builder(this)
            .setTitle("🎁 KÍCH HOẠT VÒNG QUAY 1 TỶ ĐỒNG!")
            .setMessage("Bạn đang mua 99 Hộp Blindbox! Hãy nhập MẬT MÃ 6 CHỮ SỐ BÍ MẬT để mở khóa giải thưởng 1 TỶ VNĐ!")
            .setView(etInput)
            .setPositiveButton("KÍCH HOẠT") { _, _ ->
                val enteredCode = etInput.text.toString().trim()
                if (enteredCode == MinigameManager.secretCode) {
                    MinigameManager.unlockJackpot()
                    Toast.makeText(this, "🎉 CHÚC MỪNG! BẠN ĐÃ MỞ KHÓA THÀNH CÔNG GIẢI THƯỞNG 1 TỶ ĐỒNG!", Toast.LENGTH_LONG).show()
                    completeOrder(name, address, openWheel = true)
                } else {
                    Toast.makeText(this, "❌ Mã 6 chữ số không đúng!", Toast.LENGTH_LONG).show()
                    completeOrder(name, address, openWheel = false)
                }
            }
            .setNegativeButton("Bỏ qua") { _, _ ->
                completeOrder(name, address, openWheel = false)
            }
            .setCancelable(false)
            .show()
    }

    private fun completeOrder(name: String, address: String, openWheel: Boolean = false) {
        val selectedPaymentId = binding.rgPaymentMethods.checkedRadioButtonId
        val paymentMethod = when (selectedPaymentId) {
            R.id.rbWallet -> "Ví cá nhân"
            R.id.rbShopeePay -> "Ví ShopeePay"
            R.id.rbBankTransfer -> "Chuyển khoản QR"
            else -> "Tiền mặt khi nhận hàng (COD)"
        }

        AlertDialog.Builder(this)
            .setTitle("🎉 Đặt hàng thành công!")
            .setMessage("Cảm ơn $name!\nĐơn hàng trị giá ${Food.formatVnd(CartManager.getFinalTotalPrice())} sẽ được giao tới:\n$address\n\nThanh toán: $paymentMethod")
            .setPositiveButton(if (openWheel) "MỞ VÒNG QUAY 1 TỶ NGAY!" else "Về trang chủ") { _, _ ->
                CartManager.clearCart()
                if (openWheel) {
                    val intent = Intent(this, LuckyWheelActivity::class.java)
                    startActivity(intent)
                } else {
                    val intent = Intent(this, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    startActivity(intent)
                }
                finish()
            }
            .setCancelable(false)
            .show()
    }
}