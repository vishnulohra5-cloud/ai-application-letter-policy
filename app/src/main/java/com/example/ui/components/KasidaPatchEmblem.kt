package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun KasidaPatchEmblem(
    modifier: Modifier = Modifier,
    size: Dp = 54.dp,
    showText: Boolean = false
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val cx = this.size.width / 2f
            val cy = this.size.height / 2f
            val radius = cx.coerceAtMost(cy) - 2f

            // 1. Linen cream base circle
            drawCircle(
                color = Color(0xFFFBF6ED),
                radius = radius,
                center = Offset(cx, cy)
            )

            // 2. Terracotta Rope Ring (outermost)
            val outerStroke = (radius * 0.10f).coerceAtLeast(2.5f)
            val outerDash = outerStroke * 1.5f
            drawCircle(
                color = Color(0xFFBA4A28),
                radius = radius - (outerStroke / 2f),
                center = Offset(cx, cy),
                style = Stroke(
                    width = outerStroke,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(outerDash, outerDash * 0.4f), 0f)
                )
            )

            // 3. Periwinkle Blue stitch ring
            val r2 = radius * 0.82f
            val s2 = (radius * 0.08f).coerceAtLeast(1.8f)
            drawCircle(
                color = Color(0xFF4F75A1),
                radius = r2,
                center = Offset(cx, cy),
                style = Stroke(
                    width = s2,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(s2 * 1.6f, s2 * 0.5f), 0f)
                )
            )

            // 4. Sage Green stitch ring
            val r3 = radius * 0.72f
            val s3 = (radius * 0.07f).coerceAtLeast(1.6f)
            drawCircle(
                color = Color(0xFF4C7F59),
                radius = r3,
                center = Offset(cx, cy),
                style = Stroke(
                    width = s3,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(s3 * 1.4f, s3 * 0.4f), 0f)
                )
            )

            // 5. Honey Gold stitch ring
            val r4 = radius * 0.63f
            val s4 = (radius * 0.06f).coerceAtLeast(1.5f)
            drawCircle(
                color = Color(0xFFDA8F28),
                radius = r4,
                center = Offset(cx, cy),
                style = Stroke(
                    width = s4,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(s4 * 1.3f, s4 * 0.4f), 0f)
                )
            )

            // 6. Colorful bead dots ring
            val r5 = radius * 0.54f
            val beadColors = listOf(
                Color(0xFFC43C56), Color(0xFF2EA7A0), Color(0xFFE5A429),
                Color(0xFF3D6BB3), Color(0xFFA83B8C), Color(0xFFE55829)
            )
            val beadCount = if (size >= 80.dp) 32 else 20
            val beadRadius = (radius * 0.035f).coerceAtLeast(1.4f)
            for (i in 0 until beadCount) {
                val angle = i * (2 * PI / beadCount)
                val bx = cx + r5 * cos(angle).toFloat()
                val by = cy + r5 * sin(angle).toFloat()
                drawCircle(
                    color = beadColors[i % beadColors.size],
                    radius = beadRadius,
                    center = Offset(bx, by)
                )
            }

            // 7. Inner Terracotta thread border
            val r6 = radius * 0.46f
            val s6 = (radius * 0.05f).coerceAtLeast(1.2f)
            drawCircle(
                color = Color(0xFFA23E24),
                radius = r6,
                center = Offset(cx, cy),
                style = Stroke(
                    width = s6,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(s6 * 1.2f, s6 * 0.3f), 0f)
                )
            )

            // 8. Center Parchment Disc
            drawCircle(
                color = Color(0xFFFFFDF8),
                radius = r6 - s6,
                center = Offset(cx, cy)
            )

            // 9. Embroidered Golden Fountain Pen Nib and Ink Droplet
            val nibScale = (radius * 0.45f) / 50f
            val nibX = cx + (radius * 0.08f)
            val nibY = cy - (radius * 0.05f)

            val nibPath = Path().apply {
                moveTo(nibX, nibY - (16f * nibScale))
                lineTo(nibX + (12f * nibScale), nibY + (6f * nibScale))
                lineTo(nibX + (6f * nibScale), nibY + (20f * nibScale))
                lineTo(nibX - (6f * nibScale), nibY + (18f * nibScale))
                lineTo(nibX - (12f * nibScale), nibY + (6f * nibScale))
                close()
            }

            // Fill nib with gold
            drawPath(
                path = nibPath,
                color = Color(0xFFDCA22B),
                style = Fill
            )
            // Stroke nib
            drawPath(
                path = nibPath,
                color = Color(0xFF986812),
                style = Stroke(width = (1.5f * nibScale).coerceAtLeast(1f), join = StrokeJoin.Round)
            )

            // Nib slit line & breather hole
            drawLine(
                color = Color(0xFF6B4505),
                start = Offset(nibX, nibY - (16f * nibScale)),
                end = Offset(nibX, nibY + (4f * nibScale)),
                strokeWidth = (1.2f * nibScale).coerceAtLeast(0.8f),
                cap = StrokeCap.Round
            )
            drawCircle(
                color = Color(0xFF6B4505),
                radius = (1.8f * nibScale).coerceAtLeast(1f),
                center = Offset(nibX, nibY + (4f * nibScale))
            )

            // Ink droplet
            val dropPath = Path().apply {
                val dx = nibX + (14f * nibScale)
                val dy = nibY + (16f * nibScale)
                moveTo(dx, dy)
                quadraticTo(dx + (4f * nibScale), dy + (4f * nibScale), dx + (2f * nibScale), dy + (9f * nibScale))
                quadraticTo(dx, dy + (12f * nibScale), dx - (2f * nibScale), dy + (9f * nibScale))
                close()
            }
            drawPath(path = dropPath, color = Color(0xFFB87F17), style = Fill)

            // If large size and showText is true, render Devanagari lettering on canvas
            if (showText && size >= 100.dp) {
                val nativeCanvas = drawContext.canvas.nativeCanvas
                val paint = android.graphics.Paint().apply {
                    color = android.graphics.Color.rgb(82, 40, 18)
                    textSize = 14f * (size.value / 120f)
                    typeface = android.graphics.Typeface.create(android.graphics.Typeface.SERIF, android.graphics.Typeface.BOLD)
                    isAntiAlias = true
                    textAlign = android.graphics.Paint.Align.CENTER
                }
                nativeCanvas.drawText("‘सेवा में,", cx - (radius * 0.15f), cy - (radius * 0.15f), paint)
                nativeCanvas.drawText("प्रधानाचार्य...’", cx - (radius * 0.15f), cy, paint)
                paint.textSize = 12f * (size.value / 120f)
                nativeCanvas.drawText("AI आवेदन पत्र", cx, cy + (radius * 0.28f), paint)
            }
        }
    }
}
