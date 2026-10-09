package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.local.LetterEntity
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

object PdfGenerator {

    const val PAGE_WIDTH = 595 // A4 standard width in points
    const val PAGE_HEIGHT = 842 // A4 standard height in points
    private const val MARGIN_HORIZONTAL = 54f
    private const val MARGIN_TOP = 54f

    /**
     * Strictly Black & White A4 PDF Export.
     * Contains ZERO outer borders, ZERO dotted lines, ZERO colored stamps,
     * and ZERO artificial signature boxes.
     * Plain official letter text on pure white paper.
     */
    fun generateA4Pdf(context: Context, letter: LetterEntity): File {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        // 1. Pure White Background (Clean standard blank application sheet)
        val bgPaint = Paint().apply {
            color = Color.WHITE
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, 0f, PAGE_WIDTH.toFloat(), PAGE_HEIGHT.toFloat(), bgPaint)

        // 2. Pure Black Ink Text Layout
        val contentWidth = (PAGE_WIDTH - (MARGIN_HORIZONTAL * 2)).toInt()
        val textPaint = TextPaint().apply {
            color = Color.BLACK // Strictly black & white
            textSize = 13.5f
            isAntiAlias = true
            typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL)
        }

        // Clean up brackets in placeholders for final print
        val printableContent = letter.content
            .replace("[", "")
            .replace("]", "")
            .trim()

        val staticLayout = StaticLayout.Builder
            .obtain(printableContent, 0, printableContent.length, textPaint, contentWidth)
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setLineSpacing(5f, 1.2f)
            .setIncludePad(false)
            .build()

        canvas.save()
        canvas.translate(MARGIN_HORIZONTAL, MARGIN_TOP)
        staticLayout.draw(canvas)
        canvas.restore()

        pdfDocument.finishPage(page)

        // Save PDF to cache or files dir
        val outputDir = File(context.cacheDir, "letters").apply { mkdirs() }
        val fileName = "Application_Letter_${System.currentTimeMillis()}.pdf"
        val outputFile = File(outputDir, fileName)

        FileOutputStream(outputFile).use { out ->
            pdfDocument.writeTo(out)
        }
        pdfDocument.close()

        return outputFile
    }

    fun sharePdf(context: Context, pdfFile: File) {
        try {
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                pdfFile
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "आवेदन पत्र (Application Letter)")
                putExtra(Intent.EXTRA_TEXT, "आवेदन पत्र PDF संलग्न है।")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(shareIntent, "पत्र साझा करें (Share Application Letter)"))
        } catch (e: Exception) {
            Toast.makeText(context, "साझा करने में असमर्थ: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    fun saveToDownloads(context: Context, pdfFile: File): File? {
        return try {
            val downloadsDir = context.getExternalFilesDir(null) ?: context.filesDir
            val destFile = File(downloadsDir, "Application_Letter_${System.currentTimeMillis()}.pdf")
            pdfFile.copyTo(destFile, overwrite = true)
            Toast.makeText(context, "PDF सफलतापूर्वक सहेजा गया: ${destFile.name}", Toast.LENGTH_LONG).show()
            destFile
        } catch (e: Exception) {
            Toast.makeText(context, "सहेजने में त्रुटि: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            null
        }
    }

    fun printPdf(context: Context, pdfFile: File, jobName: String = "Application_Letter") {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager
        if (printManager == null) {
            Toast.makeText(context, "प्रिंट सेवा उपलब्ध नहीं है", Toast.LENGTH_SHORT).show()
            return
        }

        val printAdapter = object : PrintDocumentAdapter() {
            override fun onLayout(
                oldAttributes: PrintAttributes?,
                newAttributes: PrintAttributes?,
                cancellationSignal: CancellationSignal?,
                callback: LayoutResultCallback?,
                extras: Bundle?
            ) {
                if (cancellationSignal?.isCanceled == true) {
                    callback?.onLayoutCancelled()
                    return
                }
                val info = PrintDocumentInfo.Builder(jobName)
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(1)
                    .build()
                callback?.onLayoutFinished(info, true)
            }

            override fun onWrite(
                pages: Array<out PageRange>?,
                destination: ParcelFileDescriptor?,
                cancellationSignal: CancellationSignal?,
                callback: WriteResultCallback?
            ) {
                var input: FileInputStream? = null
                var output: FileOutputStream? = null
                try {
                    input = FileInputStream(pdfFile)
                    output = FileOutputStream(destination?.fileDescriptor)
                    val buf = ByteArray(1024)
                    var bytesRead: Int
                    while (input.read(buf).also { bytesRead = it } > 0) {
                        output.write(buf, 0, bytesRead)
                    }
                    callback?.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
                } catch (e: Exception) {
                    callback?.onWriteFailed(e.message)
                } finally {
                    try {
                        input?.close()
                        output?.close()
                    } catch (_: Exception) {}
                }
            }
        }

        printManager.print(jobName, printAdapter, PrintAttributes.Builder().build())
    }
}
