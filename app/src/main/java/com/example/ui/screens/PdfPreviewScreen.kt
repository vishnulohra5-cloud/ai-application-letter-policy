package com.example.ui.screens

import android.app.Activity
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.LetterEntity
import com.example.ui.theme.Terracotta
import com.example.util.AdMobHelper
import com.example.util.PdfGenerator

@Composable
fun PdfPreviewScreen(
    letter: LetterEntity,
    onBack: () -> Unit,
    onSaveToArchive: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val scrollState = rememberScrollState()

    // Clean printable content without brackets
    val cleanContent = remember(letter.content) {
        letter.content.replace("[", "").replace("]", "").trim()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .verticalScroll(scrollState)
            .testTag("pdf_preview_screen")
    ) {
        // Top Header Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("pdf_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                Text(
                    text = "A4 PDF पूर्वावलोकन",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFE5DAC8).copy(alpha = 0.5f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Strict Black & White A4",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Clean A4 Sheet Preview: 100% Clean White Sheet with Black Ink Text (Zero Borders, Zero Stamps)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(2.dp))
                .clip(RoundedCornerShape(2.dp))
                .background(Color.White)
                .padding(horizontal = 24.dp, vertical = 28.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = cleanContent,
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    fontFamily = FontFamily.Serif,
                    color = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Three Action Buttons: Download, Print, Share
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 1. Download
            OutlinedButton(
                onClick = {
                    val pdfFile = PdfGenerator.generateA4Pdf(context, letter)
                    PdfGenerator.saveToDownloads(context, pdfFile)
                },
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("download_pdf_button"),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Download", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            // 2. Print
            OutlinedButton(
                onClick = {
                    val pdfFile = PdfGenerator.generateA4Pdf(context, letter)
                    PdfGenerator.printPdf(context, pdfFile, "Application_Letter_${letter.title}")
                },
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("print_pdf_button"),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Print,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Print", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            // 3. Share
            OutlinedButton(
                onClick = {
                    val pdfFile = PdfGenerator.generateA4Pdf(context, letter)
                    PdfGenerator.sharePdf(context, pdfFile)
                },
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("share_pdf_button"),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Share", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Primary Action: "सहेजें एवं सुरक्षित रखें (Save to Archive)"
        Button(
            onClick = {
                val action = {
                    val pdfFile = PdfGenerator.generateA4Pdf(context, letter)
                    PdfGenerator.saveToDownloads(context, pdfFile)
                    onSaveToArchive()
                }

                if (activity != null) {
                    AdMobHelper.showInterstitialAd(activity) {
                        action()
                    }
                } else {
                    action()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("save_high_quality_pdf_button"),
            colors = ButtonDefaults.buttonColors(containerColor = Terracotta),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "डाउनलोड व सहेजें (Download & Save PDF)",
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
