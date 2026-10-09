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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.LetterEntity
import com.example.ui.components.StitchedCard
import com.example.ui.components.stitchedBorder
import com.example.ui.theme.CrimsonRibbon
import com.example.ui.theme.SageGreen
import com.example.ui.theme.Terracotta
import com.example.ui.theme.WarmHoney
import com.example.util.PdfGenerator

@Composable
fun SavedLettersScreen(
    letters: List<LetterEntity>,
    onSelectLetter: (LetterEntity) -> Unit,
    onDeleteLetter: (LetterEntity) -> Unit,
    onWriteNewLetter: () -> Unit
) {
    val context = LocalContext.current
    var selectedFilter by remember { mutableStateOf("सभी") }

    val filterOptions = listOf("सभी", "स्वीकृत", "ड्राफ्ट", "स्कूल", "नौकरी")

    val filteredLetters = remember(letters, selectedFilter) {
        when (selectedFilter) {
            "स्वीकृत" -> letters.filter { it.status == "स्वीकृत" }
            "ड्राफ्ट" -> letters.filter { it.status == "ड्राफ्ट" }
            "स्कूल" -> letters.filter { it.category == "SICK_LEAVE" || it.category == "SCHOOL_LEAVE" || it.category == "FEE_CONCESSION" }
            "नौकरी" -> letters.filter { it.category == "JOB_APPLICATION" }
            else -> letters
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .testTag("saved_letters_screen")
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "सहेजे गए पत्र (Archive)",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "कुल ${letters.size} पत्र सुरक्षित हैं",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Filter Pills Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            filterOptions.forEach { filter ->
                val isSelected = selectedFilter == filter
                val label = if (filter == "सभी") "सभी (${letters.size})" else filter
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) CrimsonRibbon else MaterialTheme.colorScheme.surface)
                        .border(
                            1.dp,
                            if (isSelected) CrimsonRibbon else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            RoundedCornerShape(14.dp)
                        )
                        .clickable { selectedFilter = filter }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = label,
                        fontSize = 11.5.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Letters List
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredLetters, key = { it.id }) { letter ->
                SavedLetterCardItem(
                    letter = letter,
                    onClick = { onSelectLetter(letter) },
                    onShare = {
                        val pdfFile = PdfGenerator.generateA4Pdf(context, letter)
                        PdfGenerator.sharePdf(context, pdfFile)
                    },
                    onDelete = { onDeleteLetter(letter) }
                )
            }

            if (filteredLetters.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "इस श्रेणी में कोई पत्र नहीं मिला",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Bottom CTA: "+ नया प्रार्थना पत्र लिखें"
        Button(
            onClick = onWriteNewLetter,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("write_new_letter_cta"),
            colors = ButtonDefaults.buttonColors(containerColor = Terracotta),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "+ नया प्रार्थना पत्र लिखें",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SavedLetterCardItem(
    letter: LetterEntity,
    onClick: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
    val tagColor = when (letter.category) {
        "SICK_LEAVE" -> CrimsonRibbon
        "SCHOOL_LEAVE" -> WarmHoney
        "JOB_APPLICATION" -> SageGreen
        "FEE_CONCESSION" -> SageGreen
        else -> Terracotta
    }

    val tagText = when (letter.category) {
        "SICK_LEAVE" -> "स्कूल लीव"
        "SCHOOL_LEAVE" -> "पारिवारिक"
        "JOB_APPLICATION" -> "नौकरी / अनुभव"
        "FEE_CONCESSION" -> "शुल्क मुक्ति"
        else -> "आवेदन"
    }

    StitchedCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = tagColor,
        strokeWidth = 1.4.dp,
        backgroundColor = MaterialTheme.colorScheme.surface,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(tagColor.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = tagText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = tagColor
                    )
                }

                // Date
                Text(
                    text = letter.dateString.ifBlank { "हाल ही में" },
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Title
            Text(
                text = letter.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Recipient & Details
            Text(
                text = "${letter.recipient} • ${letter.applicantName}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Bottom Actions & PDF Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFE8F5E9))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = SageGreen,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "PDF सेव",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = SageGreen
                            )
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = onShare,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
