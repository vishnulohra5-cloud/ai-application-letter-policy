package com.example

import com.example.data.ai.LetterGeneratorEngine
import com.example.data.model.LetterCategory
import com.example.data.model.LetterLanguage
import com.example.data.model.LetterTone
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testHindiSickLeaveTemplate() {
        val letter = LetterGeneratorEngine.generateOfflineTemplate(
            category = LetterCategory.SICK_LEAVE,
            language = LetterLanguage.HINDI,
            tone = LetterTone.VERY_POLITE,
            recipientInput = "श्रीमान प्रधानाचार्य महोदय",
            institutionInput = "राजकीय उच्च विद्यालय",
            applicantInput = "राहुल शर्मा",
            reasonInput = "तीव्र ज्वर",
            startDate = "12 Oct",
            endDate = "15 Oct",
            days = 3,
            customPrompt = "",
            includeMedicalCert = true
        )

        assertTrue(letter.contains("सेवा में,"))
        assertTrue(letter.contains("श्रीमान प्रधानाचार्य महोदय"))
        assertTrue(letter.contains("बीमारी के कारण अवकाश"))
        assertTrue(letter.contains("राहुल शर्मा"))
    }

    @Test
    fun testEnglishJobApplicationTemplate() {
        val letter = LetterGeneratorEngine.generateOfflineTemplate(
            category = LetterCategory.JOB_APPLICATION,
            language = LetterLanguage.ENGLISH,
            tone = LetterTone.VERY_POLITE,
            recipientInput = "The HR Manager",
            institutionInput = "Tech Corp",
            applicantInput = "Amit Kumar",
            reasonInput = "Job application",
            startDate = "12 Oct",
            endDate = "15 Oct",
            days = 0,
            customPrompt = "",
            includeMedicalCert = false
        )

        assertTrue(letter.contains("To,"))
        assertTrue(letter.contains("The HR Manager"))
        assertTrue(letter.contains("Amit Kumar"))
    }

    @Test
    fun testMarathiAndBanglaTemplates() {
        val marathiLetter = LetterGeneratorEngine.generateOfflineTemplate(
            category = LetterCategory.SICK_LEAVE,
            language = LetterLanguage.MARATHI,
            tone = LetterTone.VERY_POLITE,
            recipientInput = "मुख्याध्यापक",
            institutionInput = "शाळा",
            applicantInput = "राहुल",
            reasonInput = "ताप",
            startDate = "12 Oct",
            endDate = "15 Oct",
            days = 3,
            customPrompt = "",
            includeMedicalCert = false
        )
        assertTrue(marathiLetter.contains("प्रति,"))

        val banglaLetter = LetterGeneratorEngine.generateOfflineTemplate(
            category = LetterCategory.SICK_LEAVE,
            language = LetterLanguage.BANGLA,
            tone = LetterTone.VERY_POLITE,
            recipientInput = "প্রধান শিক্ষক",
            institutionInput = "বিদ্যালয়",
            applicantInput = "রাহুল",
            reasonInput = "অসুস্থতা",
            startDate = "12 Oct",
            endDate = "15 Oct",
            days = 3,
            customPrompt = "",
            includeMedicalCert = false
        )
        assertTrue(banglaLetter.contains("বরাবর,"))
    }

    @Test
    fun testHinglishTemplate() {
        val hinglishLetter = LetterGeneratorEngine.generateOfflineTemplate(
            category = LetterCategory.SICK_LEAVE,
            language = LetterLanguage.HINGLISH,
            tone = LetterTone.VERY_POLITE,
            recipientInput = "Sriman Pradhanacharya",
            institutionInput = "School",
            applicantInput = "Rahul",
            reasonInput = "Fever",
            startDate = "12 Oct",
            endDate = "15 Oct",
            days = 3,
            customPrompt = "",
            includeMedicalCert = false
        )
        assertTrue(hinglishLetter.contains("Seva Me,"))
        assertTrue(hinglishLetter.contains("Rahul"))
        assertTrue(hinglishLetter.contains("Dhanyawad."))
    }
}
