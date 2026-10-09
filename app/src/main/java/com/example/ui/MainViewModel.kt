package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.LetterGeneratorEngine
import com.example.data.local.AppDatabase
import com.example.data.local.LetterEntity
import com.example.data.model.LetterCategory
import com.example.data.model.LetterLanguage
import com.example.data.model.LetterTone
import com.example.data.repository.LetterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen {
    DASHBOARD,
    AI_WRITER,
    EDITOR,
    PDF_PREVIEW,
    SAVED_LETTERS
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: LetterRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = LetterRepository(db.letterDao())
    }

    val savedLetters: StateFlow<List<LetterEntity>> = repository.allLetters
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentScreen = MutableStateFlow(AppScreen.DASHBOARD)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _currentLanguage = MutableStateFlow(LetterLanguage.HINDI)
    val currentLanguage: StateFlow<LetterLanguage> = _currentLanguage.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _showExitDialog = MutableStateFlow(false)
    val showExitDialog: StateFlow<Boolean> = _showExitDialog.asStateFlow()

    // Current Active Letter being edited or previewed
    private val _activeLetter = MutableStateFlow(
        LetterEntity(
            title = "बीमारी के कारण अवकाश",
            category = "SICK_LEAVE",
            recipient = "श्रीमान प्रधानाचार्य महोदय",
            institution = "राजकीय उच्च विद्यालय",
            applicantName = "राहुल शर्मा (कक्षा 10वीं - 'अ')",
            subject = "बीमारी के कारण अवकाश हेतु आवेदन",
            content = LetterGeneratorEngine.generateOfflineTemplate(
                category = LetterCategory.SICK_LEAVE,
                language = LetterLanguage.HINDI,
                tone = LetterTone.VERY_POLITE,
                recipientInput = "श्रीमान प्रधानाचार्य महोदय",
                institutionInput = "[विद्यालय का नाम]",
                applicantInput = "[राहुल]",
                reasonInput = "तीव्र ज्वर (बुखार) एवं अस्वस्थता",
                startDate = "12 Oct",
                endDate = "15 Oct",
                days = 3,
                customPrompt = "",
                includeMedicalCert = false
            ),
            language = "hi",
            dateString = "12/10/2026",
            status = "ड्राफ्ट",
            includeMedicalCert = false,
            leaveDays = 3
        )
    )
    val activeLetter: StateFlow<LetterEntity> = _activeLetter.asStateFlow()

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun setLanguage(lang: LetterLanguage) {
        _currentLanguage.value = lang
    }

    fun toggleDarkTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    fun setShowExitDialog(show: Boolean) {
        _showExitDialog.value = show
    }

    fun updateActiveLetterContent(newContent: String) {
        _activeLetter.value = _activeLetter.value.copy(content = newContent)
    }

    /**
     * Mode a) "Manually Type (मैनुअल टाइप)"
     * Opens direct form/editor for manual entry with a clean structure.
     */
    fun startManualTyping() {
        val template = when (_currentLanguage.value) {
            LetterLanguage.ENGLISH -> """To,
The Principal / Headmaster,
[School / Organization Name]

Subject: [Subject of Application]

Respected Sir/Madam,
With due respect, I wish to state that [Write your application details here].

Therefore, I kindly request you to [Write your specific request here].

Thanking you.

Yours faithfully,
[Your Name]
Class / Designation: [Class or Designation]
Date: [Date]"""

            LetterLanguage.HINGLISH -> """Seva Me,
Sriman Pradhanacharya Mahoday,
[School / Office Ka Naam]

Vishay: [Application Ka Vishay]

Mahoday,
Savinay nivedan hai ki [Apni baat yahan detail me likhein].

Aap se nivedan hai ki kripya [Apni request yahan likhein].

Dhanyawad.

Aapka aagyakari,
[Aapka Naam]
Date: [Date]"""

            else -> """सेवा में,
श्रीमान प्रधानाचार्य महोदय,
[विद्यालय / कार्यालय का नाम]

विषय: [आवेदन पत्र का विषय]

महोदय,
सविनय निवेदन है कि [अपनी बात यहाँ विस्तार से लिखें...]।

अतः आपसे विनम्र प्रार्थना है कि [अपनी प्रार्थना / अनुरोध यहाँ लिखें...]।

धन्यवाद।

भवदीय / आज्ञाकारी,
[आपका नाम]
कक्षा / पद: [कक्षा या पद]
दिनांक: [दिनांक]"""
        }

        _activeLetter.value = LetterEntity(
            title = "नया आवेदन पत्र (मैनुअल)",
            category = "CUSTOM",
            recipient = "श्रीमान प्रधानाचार्य महोदय",
            institution = "[विद्यालय / कार्यालय का नाम]",
            applicantName = "[आपका नाम]",
            subject = "आवेदन पत्र",
            content = template,
            language = _currentLanguage.value.code,
            dateString = "दिनांक",
            status = "ड्राफ्ट"
        )
        _currentScreen.value = AppScreen.EDITOR
    }

    /**
     * Mode b) "AI Type (एआई टाइप)"
     * Fast 1-line prompt or template-based instant generation.
     */
    fun startAiTyping(quickPrompt: String = "") {
        if (quickPrompt.isNotBlank()) {
            viewModelScope.launch {
                val content = LetterGeneratorEngine.generateLetter(
                    category = LetterCategory.CUSTOM,
                    language = _currentLanguage.value,
                    tone = LetterTone.VERY_POLITE,
                    recipient = "श्रीमान प्रधानाचार्य महोदय",
                    institution = "[विद्यालय / कार्यालय]",
                    applicantName = "[आपका नाम]",
                    reason = quickPrompt,
                    startDate = "12 Oct",
                    endDate = "15 Oct",
                    days = 2,
                    customPrompt = quickPrompt,
                    includeMedicalCert = false
                )
                _activeLetter.value = LetterEntity(
                    title = "एआई आवेदन पत्र",
                    category = "CUSTOM",
                    recipient = "श्रीमान प्रधानाचार्य महोदय",
                    institution = "[विद्यालय / कार्यालय]",
                    applicantName = "[आपका नाम]",
                    subject = "प्रार्थना पत्र",
                    content = content,
                    language = _currentLanguage.value.code,
                    dateString = "दिनांक",
                    status = "ड्राफ्ट"
                )
                _currentScreen.value = AppScreen.EDITOR
            }
        } else {
            _currentScreen.value = AppScreen.AI_WRITER
        }
    }

    fun selectCategoryAndDraft(category: LetterCategory) {
        viewModelScope.launch {
            val generatedContent = LetterGeneratorEngine.generateLetter(
                category = category,
                language = _currentLanguage.value,
                tone = LetterTone.VERY_POLITE,
                recipient = "श्रीमान प्रधानाचार्य महोदय",
                institution = "[विद्यालय का नाम]",
                applicantName = "राहुल शर्मा",
                reason = category.sampleQuote.replace("\"", ""),
                startDate = "12 Oct",
                endDate = "15 Oct",
                days = 3,
                includeMedicalCert = false
            )

            _activeLetter.value = LetterEntity(
                title = category.titleHi,
                category = category.name,
                recipient = "श्रीमान प्रधानाचार्य महोदय",
                institution = "[विद्यालय का नाम]",
                applicantName = "राहुल शर्मा",
                subject = "${category.titleHi} हेतु आवेदन",
                content = generatedContent,
                language = _currentLanguage.value.code,
                dateString = "12/10/2026",
                status = "ड्राफ्ट",
                includeMedicalCert = false,
                leaveDays = 3
            )
            _currentScreen.value = AppScreen.EDITOR
        }
    }

    fun generateAndOpenEditor(
        category: LetterCategory,
        language: LetterLanguage,
        tone: LetterTone,
        recipient: String,
        institution: String,
        applicant: String,
        reason: String,
        startDate: String,
        endDate: String,
        days: Int,
        customPrompt: String,
        includeMedicalCert: Boolean
    ) {
        viewModelScope.launch {
            val content = LetterGeneratorEngine.generateLetter(
                category, language, tone, recipient, institution,
                applicant, reason, startDate, endDate, days,
                customPrompt, includeMedicalCert
            )

            val newLetter = LetterEntity(
                title = category.titleHi,
                category = category.name,
                recipient = recipient,
                institution = institution,
                applicantName = applicant,
                subject = "${category.titleHi} हेतु आवेदन",
                content = content,
                language = language.code,
                dateString = "$startDate - $endDate",
                status = "ड्राफ्ट",
                includeMedicalCert = includeMedicalCert,
                leaveDays = days
            )

            _activeLetter.value = newLetter
            repository.insertLetter(newLetter)
            _currentScreen.value = AppScreen.EDITOR
        }
    }

    fun openExistingLetter(letter: LetterEntity) {
        _activeLetter.value = letter
        _currentScreen.value = AppScreen.PDF_PREVIEW
    }

    fun saveActiveLetterToArchive() {
        viewModelScope.launch {
            val letterToSave = _activeLetter.value.copy(status = "स्वीकृत")
            repository.insertLetter(letterToSave)
            _currentScreen.value = AppScreen.SAVED_LETTERS
        }
    }

    fun deleteLetter(letter: LetterEntity) {
        viewModelScope.launch {
            repository.deleteLetter(letter)
        }
    }

    fun handleBackPress(): Boolean {
        return when (_currentScreen.value) {
            AppScreen.DASHBOARD -> {
                _showExitDialog.value = true
                true
            }
            AppScreen.AI_WRITER -> {
                _currentScreen.value = AppScreen.DASHBOARD
                true
            }
            AppScreen.EDITOR -> {
                _currentScreen.value = AppScreen.DASHBOARD
                true
            }
            AppScreen.PDF_PREVIEW -> {
                _currentScreen.value = AppScreen.EDITOR
                true
            }
            AppScreen.SAVED_LETTERS -> {
                _currentScreen.value = AppScreen.DASHBOARD
                true
            }
        }
    }
}
