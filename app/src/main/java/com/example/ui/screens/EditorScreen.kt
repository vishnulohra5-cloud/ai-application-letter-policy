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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FormatAlignCenter
import androidx.compose.material.icons.filled.FormatAlignLeft
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.StitchedCard
import com.example.ui.components.StitchedDivider
import com.example.ui.components.stitchedBorder
import com.example.ui.theme.SageGreen
import com.example.ui.theme.Terracotta
import com.example.ui.theme.WarmHoney

@Composable
fun EditorScreen(
    letterContent: String,
    onContentChange: (String) -> Unit,
    onBack: () -> Unit,
    onViewA4Preview: () -> Unit
) {
    val scrollState = rememberScrollState()

    var activeDialogPlaceholder by remember { mutableStateOf<String?>(null) }
    var replacementText by remember { mutableStateOf("") }
    var showMedicalCertNote by remember { mutableStateOf(letterContent.contains("चिकित्सा") || letterContent.contains("Medical")) }

    // Find all placeholders in square brackets
    val regex = Regex("\\[(.*?)\\]")
    val placeholders = remember(letterContent) {
        regex.findAll(letterContent).map { it.value }.toSet().toList()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .testTag("editor_screen")
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("editor_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                Text(
                    text = "स्मार्ट संपादक (Editor)",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // Auto-Saved indicator chip
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE8F5E9))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = SageGreen,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "Auto-Saved",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = SageGreen
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Formatting Toolbar: B, /, U, Align, Reset
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                // Bold icon
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .clickable { /* formatting hook */ }
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("B", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                }

                // Italic
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .clickable { /* formatting hook */ }
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("/", fontStyle = androidx.compose.ui.text.font.FontStyle.Italic, fontSize = 14.sp)
                }

                // Underline
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .clickable { /* formatting hook */ }
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("U", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                // Align
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .clickable { /* formatting hook */ }
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.FormatAlignLeft,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Word count / Tag count info
            Text(
                text = "${letterContent.length} अक्षर • ${placeholders.size} टैग",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Interactive Placeholders quick-chips row
        if (placeholders.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "टैग बदलें:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = WarmHoney
                )
                Spacer(modifier = Modifier.width(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    placeholders.take(4).forEach { tag ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(WarmHoney.copy(alpha = 0.15f))
                                .border(1.dp, WarmHoney, RoundedCornerShape(12.dp))
                                .clickable {
                                    activeDialogPlaceholder = tag
                                    replacementText = tag.removeSurrounding("[", "]")
                                }
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = tag,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF7A3E1D)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Editable Letter Text Canvas / Sheet
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFFFFDF8))
                .stitchedBorder(
                    color = WarmHoney.copy(alpha = 0.7f),
                    strokeWidth = 1.4.dp,
                    cornerRadius = 10.dp
                )
                .padding(10.dp)
        ) {
            OutlinedTextField(
                value = letterContent,
                onValueChange = onContentChange,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("letter_editor_textfield"),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    fontFamily = FontFamily.Serif,
                    color = Color(0xFF261D17)
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Medical note toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFFFFBEF))
                .padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "📌 चिकित्सा प्रमाण पत्र संलग्नता का उल्लेख",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF7A3E1D)
            )

            Switch(
                checked = showMedicalCertNote,
                onCheckedChange = { checked ->
                    showMedicalCertNote = checked
                    if (checked && !letterContent.contains("चिकित्सा")) {
                        onContentChange(letterContent + "\n(चिकित्सा परामर्श प्रमाण पत्र पत्र के साथ संलग्न है।)")
                    }
                },
                colors = SwitchDefaults.colors(checkedThumbColor = WarmHoney)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Primary Action: "A4 पूर्वावलोकन देखें"
        Button(
            onClick = onViewA4Preview,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("view_a4_preview_button"),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2838)),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PictureAsPdf,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "📄 A4 पूर्वावलोकन देखें (View A4 PDF)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
    }

    // Dialog for Quick Placeholder Replacement
    activeDialogPlaceholder?.let { tag ->
        AlertDialog(
            onDismissRequest = { activeDialogPlaceholder = null },
            title = {
                Text(
                    text = "टैग संपादित करें: $tag",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column {
                    Text(
                        text = "इसके स्थान पर अपना मान दर्ज करें:",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = replacementText,
                        onValueChange = { replacementText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("placeholder_replacement_input"),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newContent = letterContent.replace(tag, replacementText)
                        onContentChange(newContent)
                        activeDialogPlaceholder = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Terracotta)
                ) {
                    Text("बदलें (Update)")
                }
            },
            dismissButton = {
                TextButton(onClick = { activeDialogPlaceholder = null }) {
                    Text("रद्द करें")
                }
            }
        )
    }
}
