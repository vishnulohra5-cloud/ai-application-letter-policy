package com.example.data.model

enum class LetterLanguage(
    val code: String,
    val displayName: String,
    val scriptName: String,
    val flag: String
) {
    HINDI("hi", "हिंदी", "Devanagari", "🇮🇳"),
    ENGLISH("en", "English", "Latin", "🌐"),
    HINGLISH("hi-Latn", "Hinglish", "Roman Script", "✍️"),
    MARATHI("mr", "मराठी", "Devanagari", "🇮🇳"),
    BANGLA("bn", "বাংলা", "Eastern Nagari", "🇮🇳");

    companion object {
        fun fromCode(code: String): LetterLanguage {
            return entries.firstOrNull {
                it.code.equals(code, ignoreCase = true) ||
                it.displayName.equals(code, ignoreCase = true) ||
                (code.equals("EN", ignoreCase = true) && it == ENGLISH)
            } ?: HINDI
        }
    }
}

enum class LetterTone(
    val titleHi: String,
    val titleEn: String,
    val description: String
) {
    VERY_POLITE("अत्यंत विनम्र", "Very Polite", "सविनय एवं सादर निवेदन शैली"),
    CONCISE("संक्षिप्त", "Concise", "सीधे मुद्दे पर केंद्रित और संक्षिप्त"),
    TRADITIONAL("पारंपरिक", "Traditional", "शास्त्रीय औपचारिक कार्यालयी शब्दावली");

    companion object {
        fun fromKey(key: String): LetterTone {
            return entries.firstOrNull { it.name.equals(key, ignoreCase = true) || it.titleHi.contains(key) } ?: VERY_POLITE
        }
    }
}
