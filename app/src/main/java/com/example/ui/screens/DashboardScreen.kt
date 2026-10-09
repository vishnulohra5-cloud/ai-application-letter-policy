package com.example.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LetterCategory
import com.example.data.model.LetterLanguage
import com.example.ui.components.KasidaPatchEmblem
import com.example.ui.components.StitchedCard
import com.example.ui.components.StitchedDivider
import com.example.ui.theme.CrimsonRibbon
import com.example.ui.theme.Periwinkle
import com.example.ui.theme.SageGreen
import com.example.ui.theme.Terracotta
import com.example.ui.theme.WarmHoney
import com.example.util.AdMobBanner

@Composable
fun DashboardScreen(
    currentLanguage: LetterLanguage,
    onLanguageChange: (LetterLanguage) -> Unit,
    onManualType: () -> Unit,
    onAiType: () -> Unit,
    onQuickPromptSubmit: (String) -> Unit,
    onSelectCategory: (LetterCategory) -> Unit
) {
    val scrollState = rememberScrollState()
    var quickPromptText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("dashboard_screen")
    ) {
        // Scrollable content area
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            // Top Warm Honey Stitched Border
            StitchedDivider(
                color = WarmHoney,
                strokeWidth = 2.dp,
                dashLength = 7.dp,
                gapLength = 4.dp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 1. Clean App Branding Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    KasidaPatchEmblem(
                        size = 50.dp,
                        showText = false
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "AI Application Letter",
                            style = MaterialTheme.typography.titleLarge,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Text(
                            text = "हस्तशिल्प धागा स्वरूप • प्रार्थना पत्र लेखक",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = Terracotta
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 2. Multi-Language Switcher (Hindi, English, Hinglish)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val languages = listOf(
                    LetterLanguage.HINDI,
                    LetterLanguage.ENGLISH,
                    LetterLanguage.HINGLISH
                )

                languages.forEach { lang ->
                    val isSelected = currentLanguage == lang
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) Terracotta else Color.Transparent)
                            .clickable { onLanguageChange(lang) }
                            .padding(vertical = 7.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = lang.flag, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = lang.displayName,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3. Two Prominent Mode Selection Buttons:
            // a) "Manually Type (मैनुअल टाइप)"
            // b) "AI Type (एआई टाइप)"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Mode a) Manually Type (मैनुअल टाइप)
                StitchedCard(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("mode_button_manual_type"),
                    borderColor = WarmHoney,
                    strokeWidth = 1.8.dp,
                    backgroundColor = Color(0xFFFFFDF8),
                    cornerRadius = 12.dp,
                    onClick = onManualType
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(WarmHoney.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.EditNote,
                                contentDescription = null,
                                tint = WarmHoney,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Manually Type",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "(मैनुअल टाइप)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = WarmHoney
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "सीधे फॉर्म व संपादक में खाली पत्र लिखें",
                            fontSize = 10.5.sp,
                            lineHeight = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "शुरू करें",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = WarmHoney
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = WarmHoney,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                }

                // Mode b) AI Type (एआई टाइप)
                StitchedCard(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("mode_button_ai_type"),
                    borderColor = Terracotta,
                    strokeWidth = 1.8.dp,
                    backgroundColor = Color(0xFFFFFDF8),
                    cornerRadius = 12.dp,
                    onClick = onAiType
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Terracotta.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Terracotta,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "AI Type",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "(एआई टाइप)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Terracotta
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "1-लाइन प्रॉम्प्ट या तुरंत एआई सृजन",
                            fontSize = 10.5.sp,
                            lineHeight = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "एआई लिखें",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Terracotta
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Terracotta,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4. Fast 1-Line AI Prompt Bar
            StitchedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("fast_prompt_bar"),
                borderColor = WarmHoney,
                strokeWidth = 1.4.dp,
                backgroundColor = Color(0xFFFFFDF9)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "⚡ त्वरित एआई प्रॉम्प्ट (Instant 1-Line Generator)",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF7A3E1D)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = quickPromptText,
                            onValueChange = { quickPromptText = it },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("fast_prompt_input"),
                            placeholder = {
                                Text(
                                    text = "जैसे: 2 दिन के लिए बीमारी अवकाश...",
                                    fontSize = 12.sp
                                )
                            },
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedBorderColor = Terracotta,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                            )
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                if (quickPromptText.isNotBlank()) {
                                    onQuickPromptSubmit(quickPromptText)
                                } else {
                                    onAiType()
                                }
                            },
                            modifier = Modifier.testTag("submit_fast_prompt_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = Terracotta),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("लिखें", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 5. Clean Category Cards Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .background(Terracotta, CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "लोकप्रिय प्रारूप (SELECT TEMPLATE)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 4 Clean Category Cards
            CategoryTile(
                title = "बीमारी के कारण अवकाश",
                subtitle = "Sick Leave Application for Principal / Headmaster",
                icon = Icons.Default.LocalHospital,
                iconTint = CrimsonRibbon,
                borderColor = CrimsonRibbon,
                onClick = { onSelectCategory(LetterCategory.SICK_LEAVE) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            CategoryTile(
                title = "विद्यालय अवकाश (School Leave)",
                subtitle = "घरेलू कार्य, विवाह समारोह अथवा आवश्यक यात्रा",
                icon = Icons.Default.School,
                iconTint = WarmHoney,
                borderColor = WarmHoney,
                onClick = { onSelectCategory(LetterCategory.SCHOOL_LEAVE) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            CategoryTile(
                title = "नौकरी / पद हेतु आवेदन",
                subtitle = "Job Application & Cover Letter with Bio-data",
                icon = Icons.Default.Work,
                iconTint = SageGreen,
                borderColor = SageGreen,
                onClick = { onSelectCategory(LetterCategory.JOB_APPLICATION) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            CategoryTile(
                title = "शासकीय एवं आधिकारिक पत्र",
                subtitle = "नगर निगम, बिजली विभाग, बैंक प्रबंधक शिकायती पत्र",
                icon = Icons.Default.AccountBalance,
                iconTint = Periwinkle,
                borderColor = Periwinkle,
                onClick = { onSelectCategory(LetterCategory.GOVERNMENT_OFFICIAL) }
            )

            Spacer(modifier = Modifier.height(14.dp))
        }

        // 6. Native AdMob Banner Integration:
        // Placed cleanly at the bottom of the front screen so it is visible immediately upon opening!
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .border(width = 1.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                .padding(vertical = 4.dp)
                .testTag("dashboard_bottom_admob_banner"),
            contentAlignment = Alignment.Center
        ) {
            AdMobBanner(
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun CategoryTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    borderColor: Color,
    onClick: () -> Unit
) {
    StitchedCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = borderColor,
        strokeWidth = 1.4.dp,
        backgroundColor = MaterialTheme.colorScheme.surface,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(iconTint.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 13.5.sp
                    )
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
