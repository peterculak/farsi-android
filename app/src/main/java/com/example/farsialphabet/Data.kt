package com.example.farsialphabet

data class FarsiLetter(
    val id: Int,
    val name: String,
    val transliteration: String,
    val fullForm: String,
    val shortForm: String
)

object LetterRepository {
    val letters = listOf(
        FarsiLetter(1, "Aleph", "A", "آ", "ا"),
        FarsiLetter(2, "Be", "B", "ب", "بـ"),
        FarsiLetter(3, "Pe", "P", "پ", "پـ"),
        FarsiLetter(4, "Te", "T", "ت", "تـ"),
        FarsiLetter(5, "Se", "S", "ث", "ثـ"),
        FarsiLetter(6, "Jim", "J", "ج", "جـ"),
        FarsiLetter(7, "Che", "CH", "چ", "چـ"),
        FarsiLetter(8, "He", "H", "ح", "حـ"),
        FarsiLetter(9, "Khe", "KH", "خ", "خـ"),
        FarsiLetter(10, "Dal", "D", "د", "ـد"),
        FarsiLetter(11, "Zal", "Z", "ذ", "ـذ"),
        FarsiLetter(12, "Re", "R", "ر", "ـر"),
        FarsiLetter(13, "Ze", "Z", "ز", "ـز"),
        FarsiLetter(14, "Zhe", "ZH", "ژ", "ـژ"),
        FarsiLetter(15, "Sin", "S", "س", "سـ"),
        FarsiLetter(16, "Shin", "SH", "ش", "شـ"),
        FarsiLetter(17, "Sad", "S", "ص", "صـ"),
        FarsiLetter(18, "Zad", "Z", "ض", "ضـ"),
        FarsiLetter(19, "Ta", "T", "ط", "طـ"),
        FarsiLetter(20, "Za", "Z", "ظ", "ظـ"),
        FarsiLetter(21, "Ain", "A/E", "ع", "عـ"),
        FarsiLetter(22, "Ghain", "GH", "غ", "غـ"),
        FarsiLetter(23, "Fe", "F", "ف", "فـ"),
        FarsiLetter(24, "Qaf", "Q", "ق", "قـ"),
        FarsiLetter(25, "Kaf", "K", "ک", "کـ"),
        FarsiLetter(26, "Gaf", "G", "گ", "گـ"),
        FarsiLetter(27, "Lam", "L", "ل", "لـ"),
        FarsiLetter(28, "Mim", "M", "م", "مـ"),
        FarsiLetter(29, "Nun", "N", "ن", "نـ"),
        FarsiLetter(30, "Vav", "V", "و", "ـو"),
        FarsiLetter(31, "He", "H", "ه", "هـ"),
        FarsiLetter(32, "Ye", "Y", "ی", "یـ")
    )
}
