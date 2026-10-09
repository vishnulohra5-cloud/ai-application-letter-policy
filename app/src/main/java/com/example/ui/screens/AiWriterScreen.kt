package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LetterCategory
import com.example.data.model.LetterLanguage
import com.example.data.model.LetterTone
import com.example.ui.components.StitchedCard
import com.example.ui.components.StitchedDivider
import com.example.ui.theme.SageGreen
import com.example.ui.theme.Terracotta
import com.example.ui.theme.WarmHoney

@Composable
fun AiWriterScreen(
    initialCategory: LetterCategory = LetterCategory.SICK_LEAVE,
    initialLanguage: LetterLanguage = LetterLanguage.HINDI,
    onBack: () -> Unit,
    onProceedToEditor: (
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
    ) -> Unit
) {
    var category by remember { mutableStateOf(initialCategory) }
    var language by remember { mutableStateOf(initialLanguage) }
    var tone by remember { mutableStateOf(LetterTone.VERY_POLITE) }

    var recipient by remember {
        mutableStateOf(
            when (initialCategory) {
                LetterCategory.JOB_APPLICATION -> "श्रीमान मानव संसाधन प्रबंधक (HR)"
                LetterCategory.GOVERNMENT_OFFICIAL -> "श्रीमान नगर आयुक्त / शाखा प्रबंधक"
                else -> "श्रीमान प्रधानाचार्य महोदय"
            }
        )
    }
    var institution by remember {
        mutableStateOf(
            when (initialCategory) {
                LetterCategory.JOB_APPLICATION -> "ग्लोबल टेक सॉल्यूशंस, नई दिल्ली"
                LetterCategory.GOVERNMENT_OFFICIAL -> "नगर निगम कार्यालय, मंडल-4"
                else -> "राजकीय उच्चतर माध्यमिक विद्यालय"
            }
        )
    }
    var applicantName by remember { mutableStateOf("राहुल शर्मा (कक्षा 10वीं - 'अ')") }
    var reason by remember {
        mutableStateOf(
            when (initialCategory) {
                LetterCategory.SICK_LEAVE -> "तीव्र ज्वर (बुखार) एवं अस्वस्थता"
                LetterCategory.SCHOOL_LEAVE -> "अति आवश्यक घरेलू पारिवारिक कार्य"
                LetterCategory.JOB_APPLICATION -> "रिक्त पद हेतु बायोडाटा व अनुभव पत्र"
                LetterCategory.GOVERNMENT_OFFICIAL -> "मार्ग मरम्मत व प्रकाश व्यवस्था सुधार"
                LetterCategory.FEE_CONCESSION -> "पारिवारिक आर्थिक विषमता व उत्कृष्ट अंक"
                else -> "विशेष अवकाश प्रार्थना"
            }
        )
    }

    var startDate by remember { mutableStateOf("12 Oct 2026") }
    var endDate by remember { mutableStateOf("15 Oct 2026") }
    var leaveDays by remember { mutableIntStateOf(3) }
    var customPrompt by remember { mutableStateOf("") }
    var includeMedicalCert by remember { mutableStateOf(initialCategory == LetterCategory.SICK_LEAVE) }
    var isGenerating by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .verticalScroll(scrollState)
            .testTag("ai_writer_screen")
    ) {
        // Header Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("writer_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            Text(
                text = "पत्र विवरण भरें (AI Drafting)",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        // Progress bar (50%)
        LinearProgressIndicator(
            progress = { 0.5f },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = WarmHoney,
            trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Language Pill Selector (Hindi, English, Hinglish)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val supportedLanguages = listOf(
                LetterLanguage.HINDI,
                LetterLanguage.ENGLISH,
                LetterLanguage.HINGLISH
            )
            supportedLanguages.forEach { lang ->
                val selected = language == lang
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (selected) Terracotta else MaterialTheme.colorScheme.surface)
                        .border(1.dp, if (selected) Terracotta else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .clickable { language = lang }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${lang.flag} ${lang.displayName}",
                        fontSize = 12.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                        color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Recipient Input
        Text(
            text = "👤 प्राप्तकर्ता (Recipient)",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = recipient,
            onValueChange = { recipient = it },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_recipient"),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = Terracotta,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Institution Input
        Text(
            text = "🏫 विद्यालय / कार्यालय (Institution)",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = institution,
            onValueChange = { institution = it },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_institution"),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = Terracotta,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Applicant Name
        Text(
            text = "✍️ आपका नाम व पद/कक्षा (Applicant)",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = applicantName,
            onValueChange = { applicantName = it },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_applicant"),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = Terracotta,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Leave Reason Selection Chips
        Text(
            text = "📋 अवकाश / आवेदन का कारण",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(6.dp))

        val reasonPresets = listOf(
            "तीव्र ज्वर / बीमारी",
            "पारिवारिक विवाह",
            "आवश्यक घरेलू कार्य",
            "शुल्क रियायत प्रार्थना",
            "अनुभव प्रमाण पत्र"
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            reasonPresets.take(3).forEach { preset ->
                val isSelected = reason.contains(preset.take(4))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .border(
                            1.dp,
                            if (isSelected) Terracotta else MaterialTheme.colorScheme.outline,
                            RoundedCornerShape(16.dp)
                        )
                        .background(if (isSelected) Terracotta.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface)
                        .clickable { reason = preset }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = preset,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Terracotta else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = reason,
            onValueChange = { reason = it },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_reason"),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = Terracotta,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            ),
            placeholder = { Text("कारण विस्तार से लिखें...") }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Dates Row
        Text(
            text = "📅 अवधि (Dates & Days)",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = startDate,
                onValueChange = { startDate = it },
                modifier = Modifier
                    .weight(1f)
                    .testTag("input_start_date"),
                label = { Text("प्रारंभ (From)", fontSize = 11.sp) },
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )

            Text("से", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)

            OutlinedTextField(
                value = endDate,
                onValueChange = { endDate = it },
                modifier = Modifier
                    .weight(1f)
                    .testTag("input_end_date"),
                label = { Text("समाप्त (To)", fontSize = 11.sp) },
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFFFF6E5))
                    .border(1.dp, WarmHoney, RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "$leaveDays दिन",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = WarmHoney
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tone Selector (अत्यंत विनम्र, संक्षिप्त, पारंपरिक)
        Text(
            text = "भाषा शैली (Tone)",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LetterTone.entries.forEach { t ->
                val selected = tone == t
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (selected) Color(0xFF2B2118) else MaterialTheme.colorScheme.surface)
                        .border(1.dp, if (selected) Color(0xFF2B2118) else MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                        .clickable { tone = t }
                        .padding(vertical = 7.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = t.titleHi,
                        fontSize = 11.5.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // One line custom prompt input
        Text(
            text = "💡 कोई विशेष निर्देश? (Optional AI Instruction)",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = customPrompt,
            onValueChange = { customPrompt = it },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_custom_prompt"),
            placeholder = { Text("जैसे: परीक्षा की तिथि नहीं छूटेगी, ससम्मान अनुरोध...") },
            shape = RoundedCornerShape(8.dp),
            minLines = 2
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Medical Certificate Checkbox
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFFFFBEF))
                .clickable { includeMedicalCert = !includeMedicalCert }
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = includeMedicalCert,
                onCheckedChange = { includeMedicalCert = it },
                colors = CheckboxDefaults.colors(checkedColor = WarmHoney)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "📌 चिकित्सा परामर्श प्रमाण पत्र संलग्नता का उल्लेख करें?",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF7A3E1D)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // AI Writing Status Card
        StitchedCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = WarmHoney,
            backgroundColor = Color(0xFFFFF9ED),
            cornerRadius = 10.dp,
            strokeWidth = 1.3.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(WarmHoney),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "एआई पत्र लिख रहा है...",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF8B470D)
                    )
                    Text(
                        text = "शुद्ध व्याकरण व सम्मान सूचक शब्दों का संयोजन",
                        fontSize = 10.5.sp,
                        color = Color(0xFF5D4037)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Primary Action: "संपादन पर आगे बढ़ें"
        Button(
            onClick = {
                onProceedToEditor(
                    category, language, tone, recipient, institution,
                    applicantName, reason, startDate, endDate, leaveDays,
                    customPrompt, includeMedicalCert
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("proceed_to_editor_button"),
            colors = ButtonDefaults.buttonColors(containerColor = Terracotta),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "✨ संपादन पर आगे बढ़ें (Open Editor)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
