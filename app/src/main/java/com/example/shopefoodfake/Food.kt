package com.example.shopefoodfake

import java.io.Serializable
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

data class Food(
    val id: Int,
    val name: String,
    val description: String,
    val price: Long,
    val ingredients: String,
    val imageResId: Int
) : Serializable {

    companion object {
        fun formatVnd(price: Long): String {
            val symbols = DecimalFormatSymbols(Locale("vi", "VN")).apply {
                groupingSeparator = '.'
            }
            val decimalFormat = DecimalFormat("#,###", symbols)
            return "${decimalFormat.format(price)} VNĐ"
        }

        fun getMockData(): List<Food> {
            return listOf(
                // Item: Phở bò
                Food(
                    id = 14,
                    name = "Phở bò truyền thống",
                    description = "Phở bò Bát Đàn đậm đà truyền thống Hà Nội với nước dùng trong vắt ngọt thanh.",
                    price = 60000L,
                    ingredients = "Bánh phở, Thịt bò tái/nạm, Nước dùng xương ống, Hành lá, Quẩy giòn",
                    imageResId = R.drawable.ic_pho
                ),
                // Item: Blindbox - Túi Mù
                Food(
                    id = 15,
                    name = "Blindbox - Túi Mù",
                    description = "Hộp quà ngẫu nhiên ẩn chứa phần thưởng bí mật!",
                    price = 10000L,
                    ingredients = "Vật phẩm quà tặng ngẫu nhiên bí mật",
                    imageResId = R.drawable.ic_blindbox
                ),

                // Các vị Pizza
                Food(
                    id = 1,
                    name = "Pizza hải sản",
                    description = "Pizza hải sản tươi sống viền phô mai béo ngậy thơm nức.",
                    price = 120000L,
                    ingredients = "Tôm, mực, phô mai mozzarella, sốt cà chua, ớt chuông, hành tây",
                    imageResId = R.drawable.ic_pizza
                ),
                Food(
                    id = 2,
                    name = "Pizza bò nướng BBQ",
                    description = "Pizza bò nướng sốt BBQ đậm đà kết hợp phô mai nung chảy.",
                    price = 135000L,
                    ingredients = "Thịt bò băm, sốt BBQ, phô mai mozzarella, ớt chuông, hành tây",
                    imageResId = R.drawable.ic_pizza
                ),
                Food(
                    id = 3,
                    name = "Pizza phô mai 4 vị",
                    description = "Pizza kết hợp 4 loại phô mai cao cấp béo ngậy ngất ngây.",
                    price = 115000L,
                    ingredients = "Phô mai Mozzarella, Cheddar, Parmesan, Gorgonzola, sốt kem",
                    imageResId = R.drawable.ic_pizza
                ),

                // Các vị Hamburger
                Food(
                    id = 4,
                    name = "Hamburger bò phô mai",
                    description = "Hamburger bò Úc nướng mọng nước kẹp phô mai chảy thơm ngon.",
                    price = 55000L,
                    ingredients = "Vỏ bánh mì nhung, Bò Úc xay, Phô mai Cheddar, Xà lách, Sốt đặc biệt",
                    imageResId = R.drawable.ic_hamburger
                ),
                Food(
                    id = 5,
                    name = "Hamburger gà giòn",
                    description = "Hamburger ức gà chiên xù giòn rụm kèm sốt cay ngọt.",
                    price = 50000L,
                    ingredients = "Bánh mì, Ức gà chiên xù, Xà lách, Sốt bơ trứng, Dưa chuột muối",
                    imageResId = R.drawable.ic_hamburger
                ),

                // Các vị Gà rán
                Food(
                    id = 6,
                    name = "Gà rán truyền thống",
                    description = "Đùi gà chiên giòn rụm lớp vỏ vàng ươm chuẩn vị.",
                    price = 75000L,
                    ingredients = "Đùi gà tươi, Bột chiên xù, Gia vị tẩm ướp truyền thống",
                    imageResId = R.drawable.ic_chicken
                ),
                Food(
                    id = 7,
                    name = "Gà rán sốt cay Hàn Quốc",
                    description = "Gà rán phủ sốt Gochujang cay ngọt đậm đà chuẩn vị Seoul.",
                    price = 85000L,
                    ingredients = "Thịt gà tươi, Sốt Gochujang cay ngọt, Mè rang, Bột chiên giòn",
                    imageResId = R.drawable.ic_chicken
                ),
                Food(
                    id = 8,
                    name = "Gà rán lắc phô mai",
                    description = "Gà giòn rụm lắc bột phô mai béo ngậy giòn ngon.",
                    price = 80000L,
                    ingredients = "Cánh gà/Đùi gà, Bột phô mai béo ngậy, Gia vị",
                    imageResId = R.drawable.ic_chicken
                ),

                // Các vị Mì cay
                Food(
                    id = 9,
                    name = "Mì cay hải sản",
                    description = "Mì cay Seoul hải sản tôm mực bắp cải tím chuẩn 7 cấp độ.",
                    price = 65000L,
                    ingredients = "Mì Koreno, Tôm tươi, Mực ống, Nấm kim châm, Bắp cải tím, Sốt cay",
                    imageResId = R.drawable.ic_noodles
                ),
                Food(
                    id = 10,
                    name = "Mì cay bò Mỹ",
                    description = "Mì cay bò Mỹ cuộn nấm kim châm nước dùng đậm đà.",
                    price = 68000L,
                    ingredients = "Mì Koreno, Thịt bò Mỹ thái mỏng, Nấm kim châm, Bắp cải, Sốt cay",
                    imageResId = R.drawable.ic_noodles
                ),

                // Các vị Trà sữa
                Food(
                    id = 11,
                    name = "Trà sữa trân châu đường đen",
                    description = "Trà sữa đậm đà hòa quyện trân châu đường đen dẻo thơm.",
                    price = 35000L,
                    ingredients = "Hồng trà, Sữa tươi Dalat Milk, Trân châu đường đen, Đá",
                    imageResId = R.drawable.ic_milktea
                ),
                Food(
                    id = 12,
                    name = "Trà sữa Thái xanh",
                    description = "Trà sữa Thái xanh thơm lừng mát lạnh kèm thạch củ năng.",
                    price = 32000L,
                    ingredients = "Trà Thái xanh, Sữa đặc, Thạch củ năng, Đá viên",
                    imageResId = R.drawable.ic_milktea
                ),
                Food(
                    id = 13,
                    name = "Trà sữa khoai môn",
                    description = "Trà sữa khoai môn béo bùi thơm ngậy thơm lừng.",
                    price = 38000L,
                    ingredients = "Cốt trà Oolong, Bột khoai môn béo bùi, Trân châu trắng, Đá",
                    imageResId = R.drawable.ic_milktea
                )
            )
        }
    }
}