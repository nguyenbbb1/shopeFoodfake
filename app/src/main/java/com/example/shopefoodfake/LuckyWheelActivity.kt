package com.example.shopefoodfake

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.shopefoodfake.databinding.ActivityLuckyWheelBinding
import kotlin.random.Random

class LuckyWheelActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLuckyWheelBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLuckyWheelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindowInsets()
        updateUI()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.wheelHeader) { view, insets ->
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

    private fun updateUI() {
        binding.tvWalletBalance.text = "💳 Số dư Ví: ${WalletManager.getFormattedBalance()}"
        binding.tvSpinCount.text = "Số lượt quay còn lại: ${MinigameManager.spinCount}"

        if (MinigameManager.isJackpotUnlocked) {
            binding.cardJackpotStatus.setCardBackgroundColor(Color.parseColor("#1B5E20"))
            binding.cardJackpotStatus.strokeColor = Color.parseColor("#76FF03")
            binding.tvJackpotStatus.text = "🔓 GIẢI 1 TỶ ĐỒNG ĐÃ MỞ KHÓA!\nBẠN CHẮC CHẮN TRÚNG GIẢI ĐẶC BIỆT 1 TỶ VNĐ TRONG LẦN QUAY NÀY!"
            binding.tvJackpotStatus.setTextColor(Color.parseColor("#B9F6CA"))
        } else {
            binding.cardJackpotStatus.setCardBackgroundColor(Color.parseColor("#212121"))
            binding.cardJackpotStatus.strokeColor = Color.parseColor("#FFC107")
            binding.tvJackpotStatus.text = "🎡 Vòng Quay May Mắn - Giải Đặc Biệt 1 TỶ ĐỒNG!"
            binding.tvJackpotStatus.setTextColor(Color.parseColor("#FFECB3"))
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.tvWalletBalance.setOnClickListener {
            val intent = Intent(this, WalletActivity::class.java)
            startActivity(intent)
        }

        binding.btnTopupWallet.setOnClickListener {
            val intent = Intent(this, WalletActivity::class.java)
            startActivity(intent)
        }

        binding.btnSpin.setOnClickListener {
            if (MinigameManager.spinCount <= 0) {
                Toast.makeText(this, "Bạn đã hết lượt quay!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            MinigameManager.useSpin()
            updateUI()

            // Determine target sector
            val targetSector = if (MinigameManager.isJackpotUnlocked) {
                0 // Sector 0 is "🏆 1 TỶ VNĐ"
            } else {
                // Pick any sector from 1 to 7 (All are "😭 Chúc bạn may mắn lần sau")
                Random.nextInt(1, binding.wheelView.prizes.size)
            }

            binding.btnSpin.isEnabled = false

            binding.wheelView.spinToSector(targetSector) { sectorIndex ->
                binding.btnSpin.isEnabled = true

                if (sectorIndex == 0) {
                    // JACKPOT WINNER! (1 TỶ VNĐ)
                    WalletManager.addBalance(MinigameManager.JACKPOT_PRIZE)
                    MinigameManager.consumeJackpot()

                    AlertDialog.Builder(this)
                        .setTitle("🎉🎉 VÔ ĐỊCH 1 TỶ ĐỒNG! 🎉🎉")
                        .setMessage("CHÚC MỪNG BẠN!\n\nBạn đã trúng Giải Đặc Biệt 1.000.000.000 VNĐ từ Vòng Quay May Mắn!\n\nSố tiền 1 Tỷ đã được cộng thẳng vào Ví cá nhân của bạn!")
                        .setPositiveButton("XEM VÍ CÁ NHÂN") { _, _ ->
                            val intent = Intent(this, WalletActivity::class.java)
                            startActivity(intent)
                        }
                        .setCancelable(false)
                        .show()
                } else {
                    // ALL OTHER SECTORS ARE "Chúc bạn may mắn lần sau"
                    AlertDialog.Builder(this)
                        .setTitle("😭 RẤT TIẾC!")
                        .setMessage("Chúc bạn may mắn lần sau!")
                        .setPositiveButton("Đồng ý") { _, _ ->
                            updateUI()
                        }
                        .show()
                }
            }
        }
    }
}