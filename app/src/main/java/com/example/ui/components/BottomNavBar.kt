package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppScreen
import com.example.ui.theme.Terracotta
import com.example.ui.theme.WarmHoney

@Composable
fun KasidaBottomNavBar(
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            )
            .padding(vertical = 4.dp)
            .testTag("bottom_nav_bar"),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tab 1: प्रारूप (Templates)
            BottomNavItem(
                title = "प्रारूप",
                icon = Icons.Default.Layers,
                isSelected = currentScreen == AppScreen.DASHBOARD,
                iconTint = WarmHoney,
                onClick = { onNavigate(AppScreen.DASHBOARD) },
                modifier = Modifier.testTag("nav_tab_templates")
            )

            // Tab 2: नया पत्र (New Letter)
            BottomNavItem(
                title = "नया पत्र",
                icon = Icons.Default.AutoAwesome,
                isSelected = currentScreen == AppScreen.AI_WRITER,
                iconTint = WarmHoney,
                onClick = { onNavigate(AppScreen.AI_WRITER) },
                modifier = Modifier.testTag("nav_tab_new_letter")
            )

            // Center Floating Quick Craft Button: लिखें
            Box(
                modifier = Modifier
                    .offset(y = (-10).dp)
                    .size(54.dp)
                    .shadow(6.dp, CircleShape)
                    .clip(CircleShape)
                    .background(WarmHoney)
                    .border(2.dp, Color(0xFFFFF9ED), CircleShape)
                    .clickable { onNavigate(AppScreen.AI_WRITER) }
                    .testTag("center_quick_action_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Quick Write",
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }

            // Tab 4: PDF पत्र (PDF Letters)
            BottomNavItem(
                title = "PDF पत्र",
                icon = Icons.Default.PictureAsPdf,
                isSelected = currentScreen == AppScreen.PDF_PREVIEW,
                iconTint = Color(0xFF4D6B9A),
                onClick = { onNavigate(AppScreen.PDF_PREVIEW) },
                modifier = Modifier.testTag("nav_tab_pdf")
            )

            // Tab 5: सहेजे गए (Saved Archive)
            BottomNavItem(
                title = "सहेजे गए",
                icon = Icons.Default.Folder,
                isSelected = currentScreen == AppScreen.SAVED_LETTERS,
                iconTint = WarmHoney,
                onClick = { onNavigate(AppScreen.SAVED_LETTERS) },
                modifier = Modifier.testTag("nav_tab_saved")
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    iconTint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = if (isSelected) Terracotta else iconTint.copy(alpha = 0.8f),
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = title,
            fontSize = 10.5.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Terracotta else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
