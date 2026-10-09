package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LetterCategory
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.components.ExitConfirmationDialog
import com.example.ui.components.KasidaBottomNavBar
import com.example.ui.screens.AiWriterScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.EditorScreen
import com.example.ui.screens.PdfPreviewScreen
import com.example.ui.screens.SavedLettersScreen
import com.example.ui.theme.AiLetterTheme
import com.example.ui.theme.WarmHoney
import com.example.util.AdMobHelper

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Pre-create WebView cache directories to prevent Chromium disk cache errors
        try {
            java.io.File(cacheDir, "WebView/Default/HTTP Cache/Code Cache/js").mkdirs()
            java.io.File(cacheDir, "WebView/Default/HTTP Cache/Code Cache/wasm").mkdirs()
        } catch (_: Throwable) {}

        // Initialize AdMob test ads
        AdMobHelper.initialize(this)
        AdMobHelper.loadInterstitialAd(this)

        setContent {
            val isDarkTheme by viewModel.isDarkTheme.collectAsState()
            val currentScreen by viewModel.currentScreen.collectAsState()
            val currentLanguage by viewModel.currentLanguage.collectAsState()
            val activeLetter by viewModel.activeLetter.collectAsState()
            val savedLetters by viewModel.savedLetters.collectAsState()
            val showExitDialog by viewModel.showExitDialog.collectAsState()

            // Back Press Handler with Exit Confirmation Dialog
            BackHandler {
                viewModel.handleBackPress()
            }

            AiLetterTheme(darkTheme = isDarkTheme) {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("main_scaffold"),
                    topBar = {
                        // Craft Mode Switcher Bar (Light / Midnight Indigo)
                        CraftModeSwitcherBar(
                            isDarkTheme = isDarkTheme,
                            onToggleTheme = { viewModel.toggleDarkTheme() }
                        )
                    },
                    bottomBar = {
                        KasidaBottomNavBar(
                            currentScreen = currentScreen,
                            onNavigate = { viewModel.navigateTo(it) }
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (currentScreen) {
                            AppScreen.DASHBOARD -> {
                                DashboardScreen(
                                    currentLanguage = currentLanguage,
                                    onLanguageChange = { viewModel.setLanguage(it) },
                                    onManualType = { viewModel.startManualTyping() },
                                    onAiType = { viewModel.startAiTyping() },
                                    onQuickPromptSubmit = { viewModel.startAiTyping(it) },
                                    onSelectCategory = { category ->
                                        viewModel.selectCategoryAndDraft(category)
                                    }
                                )
                            }

                            AppScreen.AI_WRITER -> {
                                AiWriterScreen(
                                    initialCategory = LetterCategory.fromKey(activeLetter.category),
                                    initialLanguage = currentLanguage,
                                    onBack = { viewModel.navigateTo(AppScreen.DASHBOARD) },
                                    onProceedToEditor = { category, language, tone, recipient, institution,
                                                          applicant, reason, startDate, endDate, days,
                                                          customPrompt, includeMedicalCert ->
                                        viewModel.generateAndOpenEditor(
                                            category, language, tone, recipient, institution,
                                            applicant, reason, startDate, endDate, days,
                                            customPrompt, includeMedicalCert
                                        )
                                    }
                                )
                            }

                            AppScreen.EDITOR -> {
                                EditorScreen(
                                    letterContent = activeLetter.content,
                                    onContentChange = { viewModel.updateActiveLetterContent(it) },
                                    onBack = { viewModel.navigateTo(AppScreen.AI_WRITER) },
                                    onViewA4Preview = { viewModel.navigateTo(AppScreen.PDF_PREVIEW) }
                                )
                            }

                            AppScreen.PDF_PREVIEW -> {
                                PdfPreviewScreen(
                                    letter = activeLetter,
                                    onBack = { viewModel.navigateTo(AppScreen.EDITOR) },
                                    onSaveToArchive = { viewModel.saveActiveLetterToArchive() }
                                )
                            }

                            AppScreen.SAVED_LETTERS -> {
                                SavedLettersScreen(
                                    letters = savedLetters,
                                    onSelectLetter = { letter ->
                                        viewModel.openExistingLetter(letter)
                                    },
                                    onDeleteLetter = { letter ->
                                        viewModel.deleteLetter(letter)
                                    },
                                    onWriteNewLetter = { viewModel.navigateTo(AppScreen.AI_WRITER) }
                                )
                            }
                        }

                        // Exit Confirmation Dialog
                        if (showExitDialog) {
                            ExitConfirmationDialog(
                                onConfirm = {
                                    viewModel.setShowExitDialog(false)
                                    finish()
                                },
                                onDismiss = { viewModel.setShowExitDialog(false) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CraftModeSwitcherBar(
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isDarkTheme) Color(0xFF131C2D) else Color(0xFFF3EBDB))
            .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(WarmHoney)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "कसीदा शिल्प • Craft Mode Switcher",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkTheme) Color(0xFFE2E8F0) else Color(0xFF5D4037)
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(if (isDarkTheme) Color(0xFF1F2B42) else Color(0xFFFFF8EC))
                .clickable(onClick = onToggleTheme)
                .padding(horizontal = 8.dp, vertical = 3.dp)
                .testTag("theme_switcher_toggle")
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                    contentDescription = "Toggle Theme",
                    tint = WarmHoney,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isDarkTheme) "Midnight Indigo (रात)" else "Cream Linen (दिन)",
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) Color.White else Color(0xFF8B470D)
                )
            }
        }
    }
}
