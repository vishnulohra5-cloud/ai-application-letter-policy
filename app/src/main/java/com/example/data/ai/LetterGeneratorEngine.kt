package com.example.data.ai

import com.example.BuildConfig
import com.example.data.model.LetterCategory
import com.example.data.model.LetterLanguage
import com.example.data.model.LetterTone
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object LetterGeneratorEngine {

    private val client by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    suspend fun generateLetter(
        category: LetterCategory,
        language: LetterLanguage,
        tone: LetterTone,
        recipient: String,
        institution: String,
        applicantName: String,
        reason: String,
        startDate: String,
        endDate: String,
        days: Int,
        customPrompt: String = "",
        includeMedicalCert: Boolean = false
    ): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (_: Exception) {
            ""
        }

        // Try Gemini AI if API key is provided and not placeholder
        if (apiKey.isNotBlank() && !apiKey.contains("MY_GEMINI_API_KEY") && apiKey.length > 10) {
            try {
                val promptText = buildGeminiPrompt(
                    category, language, tone, recipient, institution,
                    applicantName, reason, startDate, endDate, days,
                    customPrompt, includeMedicalCert
                )
                val aiResponse = callGeminiApi(apiKey, promptText)
                if (aiResponse.isNotBlank()) {
                    return@withContext aiResponse
                }
            } catch (_: Exception) {
                // Fall back gracefully to offline template generator
            }
        }

        // Fast, reliable offline generator
        return@withContext generateOfflineTemplate(
            category, language, tone, recipient, institution,
            applicantName, reason, startDate, endDate, days,
            customPrompt, includeMedicalCert
        )
    }

    private fun buildGeminiPrompt(
        category: LetterCategory,
        language: LetterLanguage,
        tone: LetterTone,
        recipient: String,
        institution: String,
        applicantName: String,
        reason: String,
        startDate: String,
        endDate: String,
        days: Int,
        customPrompt: String,
        includeMedicalCert: Boolean
    ): String {
        return """
        Write a complete, highly professional formal application letter in ${language.displayName} (${language.scriptName} script).
        Category: ${category.titleEn}
        Tone: ${tone.titleEn}
        Recipient: ${if (recipient.isNotBlank()) recipient else "The Principal"}
        Institution/Company: ${if (institution.isNotBlank()) institution else "School / Office"}
        Applicant Name: ${if (applicantName.isNotBlank()) applicantName else "Applicant Name"}
        Reason: ${if (reason.isNotBlank()) reason else "Leave for personal/medical work"}
        Leave Duration: $days days (From $startDate to $endDate)
        Include Medical Certificate reference: $includeMedicalCert
        Additional instructions: $customPrompt
        
        Important formatting guidelines:
        1. Follow traditional formal standard letter layout:
           - Recipient address at the top
           - Formal Subject line (विषय / Subject)
           - Formal Salutation
           - Crisp, respectful body paragraphs
           - Formal closing (सधन्यवाद / Thanking you)
           - Applicant details & Date
        2. Put editable placeholder fields inside square brackets like [विद्यालय का नाम], [नाम], [दिनांक], etc.
        3. Do not include markdown codeblocks (```), return raw plain letter text.
        """.trimIndent()
    }

    private fun callGeminiApi(apiKey: String, prompt: String): String {
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
        val jsonPayload = JSONObject().apply {
            val contentsArray = org.json.JSONArray().apply {
                val contentObj = JSONObject().apply {
                    val partsArray = org.json.JSONArray().apply {
                        put(JSONObject().put("text", prompt))
                    }
                    put("parts", partsArray)
                }
                put(contentObj)
            }
            put("contents", contentsArray)
        }

        val request = Request.Builder()
            .url(url)
            .post(jsonPayload.toString().toRequestBody("application/json".toMediaType()))
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return ""
            val bodyString = response.body?.string() ?: return ""
            val root = JSONObject(bodyString)
            val candidates = root.optJSONArray("candidates") ?: return ""
            if (candidates.length() == 0) return ""
            val content = candidates.getJSONObject(0).optJSONObject("content") ?: return ""
            val parts = content.optJSONArray("parts") ?: return ""
            if (parts.length() == 0) return ""
            return parts.getJSONObject(0).optString("text", "")
        }
    }

    fun generateOfflineTemplate(
        category: LetterCategory,
        language: LetterLanguage,
        tone: LetterTone,
        recipientInput: String,
        institutionInput: String,
        applicantInput: String,
        reasonInput: String,
        startDate: String,
        endDate: String,
        days: Int,
        customPrompt: String,
        includeMedicalCert: Boolean
    ): String {
        val recipient = recipientInput.ifBlank { "श्रीमान प्रधानाचार्य महोदय" }
        val institution = institutionInput.ifBlank { "[विद्यालय / संस्थान का नाम]" }
        val applicant = applicantInput.ifBlank { "[आपका नाम, कक्षा / पद]" }
        val sDate = startDate.ifBlank { "12 Oct" }
        val eDate = endDate.ifBlank { "15 Oct" }
        val dCount = if (days > 0) days else 3

        return when (language) {
            LetterLanguage.HINDI -> generateHindiLetter(category, recipient, institution, applicant, reasonInput, sDate, eDate, dCount, includeMedicalCert)
            LetterLanguage.ENGLISH -> generateEnglishLetter(category, recipient, institution, applicant, reasonInput, sDate, eDate, dCount, includeMedicalCert)
            LetterLanguage.MARATHI -> generateMarathiLetter(category, recipient, institution, applicant, reasonInput, sDate, eDate, dCount, includeMedicalCert)
            LetterLanguage.BANGLA -> generateBanglaLetter(category, recipient, institution, applicant, reasonInput, sDate, eDate, dCount, includeMedicalCert)
            LetterLanguage.HINGLISH -> generateHinglishLetter(category, recipient, institution, applicant, reasonInput, sDate, eDate, dCount, includeMedicalCert)
        }
    }

    private fun generateHindiLetter(
        category: LetterCategory,
        recipient: String,
        institution: String,
        applicant: String,
        reason: String,
        sDate: String,
        eDate: String,
        days: Int,
        includeMedicalCert: Boolean
    ): String {
        return when (category) {
            LetterCategory.SICK_LEAVE -> """सेवा में,
$recipient,
$institution

विषय: बीमारी के कारण अवकाश हेतु आवेदन।

महोदय,
सविनय निवेदन है कि मैं $applicant आपके संस्थान का छात्र / कर्मी हूँ। मेरी तबीयत अचानक अत्यंत खराब हो जाने तथा तीव्र ज्वर (बुखार) से पीड़ित होने के कारण मैं उपस्थित होने में असमर्थ हूँ। चिकित्सक ने मुझे पूर्ण स्वास्थ्य लाभ हेतु विश्राम का परामर्श दिया है।${if (includeMedicalCert) "\n(चिकित्सा परामर्श प्रमाण पत्र पत्र के साथ संलग्न है।)" else ""}

अतः आपसे विनम्र प्रार्थना है कि मुझे दिनांक [$sDate] से [$eDate] तक (कुल $days दिन) का अवकाश प्रदान करने की कृपा करें।

धन्यवाद।

भवदीय / आज्ञाकारी,
$applicant
दिनांक: [$sDate/2026]"""

            LetterCategory.SCHOOL_LEAVE -> """सेवा में,
$recipient,
$institution

विषय: आवश्यक घरेलू कार्य / पारिवारिक समारोह हेतु विद्यालय अवकाश।

महोदय,
सादर निवेदन है कि मुझे घर पर एक अति आवश्यक निजी व पारिवारिक कार्य आ पड़ा है, जिसमें मेरी उपस्थिति अपरिहार्य है। इस कारणवश मैं आगामी कुछ दिनों तक विद्यालय में उपस्थित नहीं हो सकूँगा।

अतः प्रार्थना है कि मुझे दिनांक [$sDate] से [$eDate] तक (कुल $days दिन) का अवकाश स्वीकृत करने की कृपा करें। विद्यालय लौटने पर मैं अपना छूटा हुआ समस्त पाठ्यक्रम शीघ्र पूर्ण कर लूँगा।

सधन्यवाद।

आपका आज्ञाकारी शिष्य,
$applicant
दिनांक: [$sDate/2026]"""

            LetterCategory.JOB_APPLICATION -> """सेवा में,
$recipient,
$institution

विषय: रिक्त पद हेतु आवेदन पत्र एवं बायोडाटा प्रस्तुति।

महोदय,
सादर निवेदन है कि आपके प्रतिष्ठित संस्थान द्वारा प्रकाशित विज्ञापन के संदर्भ में, मैं इस पद हेतु अपनी उम्मीदवारी प्रस्तुत कर रहा हूँ। मेरे पास आवश्यक शैक्षणिक योग्यता एवं प्रासंगिक कार्य अनुभव उपलब्ध है।

मैं संस्थान के लक्ष्यों के अनुरूप पूरी निष्ठा और कर्तव्यनिष्ठा के साथ कार्य करने के लिए तत्पर हूँ। मेरा विस्तृत बायोडाटा (CV) संलग्न है।

साक्षात्कार का एक अवसर प्रदान करने की कृपा करें।

सधन्यवाद।

प्रार्थी,
$applicant
दूरभाष: [आपका मोबाइल नंबर]
दिनांक: [$sDate/2026]"""

            LetterCategory.GOVERNMENT_OFFICIAL -> """सेवा में,
$recipient,
$institution

विषय: जनसमस्या समाधान एवं आधिकारिक कार्यवाही हेतु प्रार्थना पत्र।

महोदय,
सविनय निवेदन है कि मैं इस क्षेत्र का स्थायी निवासी हूँ। हमारे क्षेत्र में आवश्यक नागरिक सुविधाओं से संबंधित गंभीर समस्या उत्पन्न हो गई है, जिससे समस्त नागरिकों को भारी असुविधा का सामना करना पड़ रहा है। पूर्व में भी इस संबंध में सूचित किया जा चुका है।

अतः आपसे करबद्ध निवेदन है कि जनहित को ध्यान में रखते हुए इस समस्या का त्वरित संज्ञान लेकर उचित कार्यवाही करने की कृपा करें।

धन्यवाद।

विनीत नागरिक,
$applicant
पता: [आपका पता एवं मोहल्ला]
दिनांक: [$sDate/2026]"""

            LetterCategory.FEE_CONCESSION -> """सेवा में,
$recipient,
$institution

विषय: शिक्षण शुल्क में रियायत / पूर्ण मुक्ति हेतु प्रार्थना पत्र।

महोदय,
सविनय निवेदन है कि मैं $applicant आपके विद्यालय का नियमित व परिश्रमी छात्र हूँ। मेरे पिताजी एक साधारण आय वाले व्यक्ति हैं और वर्तमान आर्थिक कठिनाइयों के कारण पूरा शिक्षण शुल्क एकमुश्त जमा कराने में असमर्थ हैं।

मेरा शैक्षणिक रिकॉर्ड सदैव उत्कृष्ट रहा है। अतः आपसे विनम्र प्रार्थना है कि मेरा शिक्षण शुल्क माफ करने की कृपा करें।

धन्यवाद।

आपका आज्ञाकारी छात्र,
$applicant
दिनांक: [$sDate/2026]"""

            LetterCategory.CUSTOM -> """सेवा में,
$recipient,
$institution

विषय: आवश्यक कार्य हेतु औपचारिक आवेदन पत्र।

महोदय,
सविनय निवेदन है कि $applicant द्वारा यह पत्र विशेष संदर्भ में प्रस्तुत किया जा रहा है। ${reason.ifBlank { "मुझे व्यक्तिगत आवश्यक कार्य हेतु स्वीकृति की आवश्यकता है।" }}

अतः आपसे सादर अनुरोध है कि कृपया उक्त विषय पर सहानुभूतिपूर्वक विचार करते हुए आवश्यक अनुमति प्रदान करने की कृपा करें।

सधन्यवाद।

प्रार्थी,
$applicant
दिनांक: [$sDate/2026]"""
        }
    }

    private fun generateEnglishLetter(
        category: LetterCategory,
        recipient: String,
        institution: String,
        applicant: String,
        reason: String,
        sDate: String,
        eDate: String,
        days: Int,
        includeMedicalCert: Boolean
    ): String {
        val rec = if (recipient.contains("श्रीमान")) "The Principal / Manager" else recipient
        return """To,
$rec,
$institution

Subject: Application for ${category.titleEn}.

Respected Sir/Madam,
With due respect, I beg to state that I am $applicant. Due to ${reason.ifBlank { "unforeseen urgent circumstances" }}, I will not be able to attend from [$sDate] to [$eDate] (total $days days).${if (includeMedicalCert) "\n(Medical certificate is attached herewith for your kind perusal.)" else ""}

Therefore, I kindly request you to grant me leave for the specified duration. I assure you that I will cover all missed academic / official work promptly upon my return.

Thanking you.

Yours faithfully,
$applicant
Date: [$sDate/2026]"""
    }

    private fun generateMarathiLetter(
        category: LetterCategory,
        recipient: String,
        institution: String,
        applicant: String,
        reason: String,
        sDate: String,
        eDate: String,
        days: Int,
        includeMedicalCert: Boolean
    ): String {
        return """प्रति,
मा. मुख्याध्यापक महोदय,
$institution

विषय: ${category.titleHi} रजेचा अर्ज.

महोदय,
सविनय विनंती विशेष की, मी $applicant आपल्या शाळेत शिकत आहे. मला अचानक अस्वस्थ वाटू लागल्यामुळे मी शाळेत उपस्थित राहू शकत नाही. डॉक्टरांनी मला पूर्ण विश्रांती घेण्याचा सल्ला दिला आहे.${if (includeMedicalCert) "\n(वैद्यकीय प्रमाणपत्र सोबत जोडलेले आहे.)" else ""}

तरी कृपया मला दिनांक [$sDate] ते [$eDate] पर्यंत (एकूण $days दिवस) रजा मंजूर करावी ही नम्र विनंती.

आपला आज्ञाधारक विद्यार्थी,
$applicant
दिनांक: [$sDate/2026]"""
    }

    private fun generateBanglaLetter(
        category: LetterCategory,
        recipient: String,
        institution: String,
        applicant: String,
        reason: String,
        sDate: String,
        eDate: String,
        days: Int,
        includeMedicalCert: Boolean
    ): String {
        return """বরাবর,
মাননীয় প্রধান শিক্ষক মহাশয়,
$institution

বিষয়: ছুটির জন্য আবেদন পত্র।

মহাশয়,
বিনীত নিবেদন এই যে, আমি $applicant আপনার বিদ্যালয়ের ছাত্র। হঠাৎ অসুস্থ হয়ে পড়ার কারণে আমি বিদ্যালয়ে উপস্থিত হতে পারছি না। ডাক্তার আমাকে সম্পূর্ণ বিশ্রামে থাকার পরামর্শ দিয়েছেন।${if (includeMedicalCert) "\n(চিকিৎসকের প্রেসক্রিপশন ও সার্টিফিকেট সংযুক্ত করা হলো।)" else ""}

অতএব, মহাশয়ের নিকট বিনীত প্রার্থনা এই যে, আমাকে [$sDate] হইতে [$eDate] পর্যন্ত (মোট $days দিন) ছুটি মঞ্জুর করিয়া বাধিত করিবেন।

ধন্যবাদান্তে,
আপনার অনুগত ছাত্র,
$applicant
তারিখ: [$sDate/2026]"""
    }

    private fun generateHinglishLetter(
        category: LetterCategory,
        recipient: String,
        institution: String,
        applicant: String,
        reason: String,
        sDate: String,
        eDate: String,
        days: Int,
        includeMedicalCert: Boolean
    ): String {
        return """Seva Me,
$recipient,
$institution

Vishay: ${category.titleHi} ke liye application.

Mahoday,
Savinay nivedan hai ki main $applicant aapke vidyalaya / sansthan ka chhatra hoon. Meri tabiyat achanak kharab ho jane ke karan main aane me asamarth hoon. Doctor ne mujhe complete rest karne ki salah di hai.${if (includeMedicalCert) "\n(Medical certificate sath me attached hai.)" else ""}

Aap se nivedan hai ki kripya mujhe [$sDate] se [$eDate] tak ($days din) ka leave provide karne ki kripa karein.

Dhanyawad.

Aapka aagyakari,
$applicant
Date: [$sDate/2026]"""
    }
}
