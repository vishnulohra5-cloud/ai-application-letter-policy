package com.example.data.model

enum class LetterCategory(
    val titleHi: String,
    val titleEn: String,
    val subtitleHi: String,
    val sampleQuote: String,
    val isPopular: Boolean = false
) {
    SICK_LEAVE(
        titleHi = "बीमारी के कारण अवकाश",
        titleEn = "Sick Leave Application",
        subtitleHi = "Sick Leave Application for Principal / Headmaster",
        sampleQuote = "\"सविनय निवेदन है कि मेरी तबीयत खराब होने के कारण...\"",
        isPopular = true
    ),
    SCHOOL_LEAVE(
        titleHi = "विद्यालय अवकाश (School Leave)",
        titleEn = "School Leave Application",
        subtitleHi = "ज़रूरी घरेलू कार्य, विवाह समारोह अथवा यात्रा हेतु",
        sampleQuote = "\"महोदय, मुझे घर पर अति आवश्यक कार्य होने के कारण...\"",
        isPopular = false
    ),
    JOB_APPLICATION(
        titleHi = "नौकरी / पद हेतु आवेदन",
        titleEn = "Job & Post Application",
        subtitleHi = "Job Application, Cover Letter & Bio-data attachment",
        sampleQuote = "\"मान्यवर, आपके प्रतिष्ठित संस्थान में रिक्त पद हेतु सविनय आवेदन...\"",
        isPopular = false
    ),
    GOVERNMENT_OFFICIAL(
        titleHi = "शासकीय एवं आधिकारिक पत्र",
        titleEn = "Official & Govt Letters",
        subtitleHi = "नगर निगम, बिजली विभाग, बैंक प्रबंधक शिकायती पत्र",
        sampleQuote = "\"सेवा में, श्रीमान शाखा प्रबंधक / नगर आयुक्त महोदय...\"",
        isPopular = false
    ),
    FEE_CONCESSION(
        titleHi = "शुल्क मुक्ति / छात्रवृत्ति प्रार्थना पत्र",
        titleEn = "Fee Concession & Scholarship",
        subtitleHi = "आर्थिक स्थिति एवं उत्कृष्ट परीक्षा परिणाम के आधार पर",
        sampleQuote = "\"सविनय निवेदन है कि मेरे पिताजी की अल्प आय के कारण...\"",
        isPopular = false
    ),
    CUSTOM(
        titleHi = "कस्टम / विविध पत्र",
        titleEn = "Custom / Other Purpose",
        subtitleHi = "अपनी आवश्यकता अनुसार मनचाहा पत्र तैयार करें",
        sampleQuote = "\"सेवा में, महोदय...\"",
        isPopular = false
    );

    companion object {
        fun fromKey(key: String): LetterCategory {
            return entries.firstOrNull { it.name.equals(key, ignoreCase = true) || it.titleHi.contains(key) } ?: SICK_LEAVE
        }
    }
}
