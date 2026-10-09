package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CreamLinenSurface
import com.example.ui.theme.Terracotta
import com.example.ui.theme.WarmHoney

fun Modifier.stitchedBorder(
    color: Color = Terracotta,
    strokeWidth: Dp = 1.5.dp,
    dashLength: Dp = 6.dp,
    gapLength: Dp = 3.dp,
    cornerRadius: Dp = 12.dp
): Modifier = this.drawBehind {
    val strokeWidthPx = strokeWidth.toPx()
    val halfStroke = strokeWidthPx / 2f
    val cornerRadiusPx = cornerRadius.toPx()
    val dashPx = dashLength.toPx()
    val gapPx = gapLength.toPx()

    val path = Path().apply {
        addRoundRect(
            RoundRect(
                rect = Rect(
                    offset = Offset(halfStroke, halfStroke),
                    size = Size(size.width - strokeWidthPx, size.height - strokeWidthPx)
                ),
                cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx)
            )
        )
    }

    drawPath(
        path = path,
        color = color,
        style = Stroke(
            width = strokeWidthPx,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashPx, gapPx), 0f)
        )
    )
}

@Composable
fun StitchedCard(
    modifier: Modifier = Modifier,
    borderColor: Color = Terracotta,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    cornerRadius: Dp = 12.dp,
    strokeWidth: Dp = 1.6.dp,
    dashLength: Dp = 6.dp,
    gapLength: Dp = 3.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val clickableModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick
        )
    } else Modifier

    Box(
        modifier = modifier
            .background(backgroundColor, RoundedCornerShape(cornerRadius))
            .stitchedBorder(
                color = borderColor,
                strokeWidth = strokeWidth,
                dashLength = dashLength,
                gapLength = gapLength,
                cornerRadius = cornerRadius
            )
            .then(clickableModifier)
    ) {
        content()
    }
}

@Composable
fun StitchedDivider(
    modifier: Modifier = Modifier,
    color: Color = WarmHoney,
    strokeWidth: Dp = 1.5.dp,
    dashLength: Dp = 6.dp,
    gapLength: Dp = 3.dp
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(strokeWidth + 2.dp)
            .padding(vertical = 1.dp)
    ) {
        val strokePx = strokeWidth.toPx()
        val y = size.height / 2f
        drawLine(
            color = color,
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = strokePx,
            pathEffect = PathEffect.dashPathEffect(
                floatArrayOf(dashLength.toPx(), gapLength.toPx()),
                0f
            )
        )
    }
}
