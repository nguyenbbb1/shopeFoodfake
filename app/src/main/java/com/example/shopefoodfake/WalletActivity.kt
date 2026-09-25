package com.example.shopefoodfake

import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.shopefoodfake.databinding.ActivityWalletBinding
import kotlin.random.Random

class WalletActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWalletBinding

    data class MathQuestion(
        val questionText: String,
        val correctAnswer: Int
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWalletBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindowInsets()
        updateBalanceUI()
        setupListeners()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.walletHeader) { view, insets ->
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

    private fun updateBalanceUI() {
        binding.tvWalletBalance.text = WalletManager.getFormattedBalance()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnDeposit.setOnClickListener {
            showDepositAmountDialog()
        }

        binding.btnWithdraw.setOnClickListener {
            showWithdrawAmountDialog()
        }
    }

    // ==================== DEPOSIT FLOW ====================

    private fun showDepositAmountDialog() {
        val etAmount = EditText(this).apply {
            hint = "Nhập số tiền muốn nạp (VNĐ)..."
            inputType = InputType.TYPE_CLASS_NUMBER
            textSize = 18f
            gravity = Gravity.CENTER
            setPadding(32, 24, 32, 24)
        }

        AlertDialog.Builder(this)
            .setTitle("➕ Nạp tiền vào Ví")
            .setMessage("Nhập số tiền bạn muốn nạp. Bạn cần GIẢI ĐÚNG 5 CÂU TOÁN TIỂU HỌC.\n\n⚠️ CẢNH BÁO: Nếu giải sai dù chỉ 1 câu, TOÀN BỘ SỐ TIỀN TRONG VÍ SẼ VỀ 0 VNĐ!")
            .setView(etAmount)
            .setPositiveButton("NẠP") { _, _ ->
                val amountStr = etAmount.text.toString().trim()
                val amount = amountStr.toLongOrNull()
                if (amount == null || amount <= 0) {
                    Toast.makeText(this, "Số tiền nạp không hợp lệ!", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                startMathChallenge(amount)
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun startMathChallenge(depositAmount: Long) {
        val questions = generateMathQuestions(5) // Reduced to 5 questions
        askMathQuestion(0, questions, depositAmount)
    }

    private fun askMathQuestion(index: Int, questions: List<MathQuestion>, depositAmount: Long) {
        if (index >= questions.size) {
            // All 5 questions passed!
            WalletManager.addBalance(depositAmount)
            updateBalanceUI()
            AlertDialog.Builder(this)
                .setTitle("🎉 GIẢI TOÁN THÀNH CÔNG!")
                .setMessage("Chúc mừng! Bạn đã giải đúng cả 5 câu toán tiểu học!\n\nĐã nạp thành công ${Food.formatVnd(depositAmount)} vào Ví cá nhân!")
                .setPositiveButton("Tuyệt vời", null)
                .show()
            return
        }

        val q = questions[index]
        val etAnswer = EditText(this).apply {
            hint = "Nhập đáp số..."
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED
            textSize = 20f
            gravity = Gravity.CENTER
            setPadding(32, 24, 32, 24)
        }

        AlertDialog.Builder(this)
            .setTitle("🧠 Câu ${index + 1}/5: Giải toán tiểu học")
            .setMessage("Mời bạn tính:\n\n👉  ${q.questionText} = ?")
            .setView(etAnswer)
            .setPositiveButton("Trả lời") { _, _ ->
                val userAnswer = etAnswer.text.toString().trim().toIntOrNull()
                if (userAnswer == q.correctAnswer) {
                    Toast.makeText(this, "✅ Chính xác! Sang câu tiếp theo...", Toast.LENGTH_SHORT).show()
                    askMathQuestion(index + 1, questions, depositAmount)
                } else {
                    // ANSWERED WRONG: RESET BALANCE TO 0!
                    WalletManager.resetBalance()
                    updateBalanceUI()
                    AlertDialog.Builder(this)
                        .setTitle("❌ BẠN ĐÃ TÍNH SAI! THẤT BẠI HOÀN TOÀN!")
                        .setMessage("Rất tiếc! Đáp án đúng của câu '${q.questionText}' phải là ${q.correctAnswer} (bạn trả lời $userAnswer).\n\n⚠️ Vì bạn giải sai toán tiểu học nên TOÀN BỘ SỐ TIỀN TRONG VÍ ĐÃ BỊ PHẠT VỀ 0 VNĐ!")
                        .setPositiveButton("Rút kinh nghiệm", null)
                        .setCancelable(false)
                        .show()
                }
            }
            .setCancelable(false)
            .show()
    }

    private fun generateMathQuestions(count: Int): List<MathQuestion> {
        val list = mutableListOf<MathQuestion>()
        for (i in 0 until count) {
            val op = Random.nextInt(3) // 0: +, 1: -, 2: *
            when (op) {
                0 -> {
                    val a = Random.nextInt(5, 50)
                    val b = Random.nextInt(5, 50)
                    list.add(MathQuestion("$a + $b", a + b))
                }
                1 -> {
                    val a = Random.nextInt(20, 80)
                    val b = Random.nextInt(1, a)
                    list.add(MathQuestion("$a - $b", a - b))
                }
                else -> {
                    val a = Random.nextInt(2, 9)
                    val b = Random.nextInt(2, 9)
                    list.add(MathQuestion("$a × $b", a * b))
                }
            }
        }
        return list
    }

    // ==================== WITHDRAW FLOW ====================

    private fun showWithdrawAmountDialog() {
        if (WalletManager.balance <= 0) {
            Toast.makeText(this, "Ví của bạn đang có 0 VNĐ, không thể rút tiền!", Toast.LENGTH_SHORT).show()
            return
        }

        val etAmount = EditText(this).apply {
            hint = "Nhập số tiền muốn rút (VNĐ)..."
            inputType = InputType.TYPE_CLASS_NUMBER
            textSize = 18f
            gravity = Gravity.CENTER
            setPadding(32, 24, 32, 24)
        }

        AlertDialog.Builder(this)
            .setTitle("💸 Rút tiền về Ngân hàng")
            .setMessage("Nhập số tiền bạn muốn rút (Số dư hiện tại: ${WalletManager.getFormattedBalance()}):")
            .setView(etAmount)
            .setPositiveButton("RÚT TIỀN") { _, _ ->
                val amountStr = etAmount.text.toString().trim()
                val amount = amountStr.toLongOrNull()

                if (amount == null || amount <= 0 || amount > WalletManager.balance) {
                    Toast.makeText(this, "Số tiền rút không hợp lệ hoặc vượt quá số dư!", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                askBeautyQuestion(amount)
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun askBeautyQuestion(withdrawAmount: Long) {
        AlertDialog.Builder(this)
            .setTitle("❓ XÁC NHẬN THỰC THÀ (CÂU HỎI QUYẾT ĐỊNH)")
            .setMessage("Để xác nhận rút ${Food.formatVnd(withdrawAmount)}, hãy trả lời thật lòng:\n\n👉 CHỦ APP CÓ ĐẸP TRAI KHÔNG?")
            .setPositiveButton("CÓ (ĐẸP TRAI 🥰)") { _, _ ->
                // Answered YES!
                WalletManager.deductBalance(withdrawAmount)
                updateBalanceUI()
                AlertDialog.Builder(this)
                    .setTitle("🎉 RÚT TIỀN THÀNH CÔNG!")
                    .setMessage("Cảm ơn sự khen ngợi chân thành của bạn! 🥰\n\nĐã rút ${Food.formatVnd(withdrawAmount)} về tài khoản ngân hàng của bạn!")
                    .setPositiveButton("Tuyệt vời", null)
                    .show()
            }
            .setNegativeButton("KHÔNG (XẤU TRAI 🤮)") { _, _ ->
                // Answered NO! PENALTY RESET TO 0!
                WalletManager.resetBalance()
                updateBalanceUI()
                AlertDialog.Builder(this)
                    .setTitle("❌ BẠN ĐÃ TRẢ LỜI KHÔNG!")
                    .setMessage("CẢNH BÁO! Vì bạn dám chê chủ app KHÔNG đẹp trai nên TOÀN BỘ SỐ TIỀN TRONG VÍ CỦA BẠN ĐÃ BỊ PHẠT VỀ 0 VNĐ!\n\nHãy rút kinh nghiệm cho lần sau!")
                    .setPositiveButton("Thôi xong...", null)
                    .setCancelable(false)
                    .show()
            }
            .setCancelable(false)
            .show()
    }
}