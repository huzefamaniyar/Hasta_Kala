package com.example.hasta_kala.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.widget.Toast
import com.example.hasta_kala.data.db.entities.SaleWithItems
import java.io.File
import java.io.FileOutputStream

object PdfExportHelper {

    fun generateBillPdf(context: Context, saleWithItems: SaleWithItems) {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(300, 600, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas
        val paint = Paint()

        var y = 40f
        
        // Header
        paint.textSize = 18f
        paint.isFakeBoldText = true
        canvas.drawText("HASTA-KALA", 80f, y, paint)
        
        y += 30f
        paint.textSize = 12f
        paint.isFakeBoldText = false
        canvas.drawText("Bill No: ${saleWithItems.sale.billNumber}", 20f, y, paint)
        
        y += 20f
        canvas.drawText("Date: ${java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(java.util.Date(saleWithItems.sale.createdAt))}", 20f, y, paint)
        
        y += 40f
        paint.isFakeBoldText = true
        canvas.drawText("Items", 20f, y, paint)
        canvas.drawText("Qty", 180f, y, paint)
        canvas.drawText("Price", 240f, y, paint)
        
        y += 10f
        canvas.drawLine(20f, y, 280f, y, paint)
        
        y += 20f
        paint.isFakeBoldText = false
        saleWithItems.items.forEach { item ->
            canvas.drawText(item.productName.take(15), 20f, y, paint)
            canvas.drawText("${item.quantity}", 180f, y, paint)
            canvas.drawText("₹${item.subtotal}", 240f, y, paint)
            y += 20f
        }
        
        y += 20f
        canvas.drawLine(20f, y, 280f, y, paint)
        
        y += 30f
        paint.isFakeBoldText = true
        canvas.drawText("Total Amount:", 20f, y, paint)
        canvas.drawText("₹${saleWithItems.sale.totalAmount}", 200f, y, paint)
        
        y += 40f
        paint.textSize = 10f
        paint.isFakeBoldText = false
        canvas.drawText("Thank you for supporting local artisans!", 40f, y, paint)

        pdfDocument.finishPage(page)

        savePdfToDownloads(context, pdfDocument, "Bill_${saleWithItems.sale.billNumber}.pdf")
    }

    fun generateAnalyticsPdf(context: Context, state: com.example.hasta_kala.ui.screens.dashboard.DashboardState) {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(300, 600, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas
        val paint = Paint()

        var y = 40f
        paint.textSize = 18f
        paint.isFakeBoldText = true
        canvas.drawText("HASTA-KALA ANALYTICS", 40f, y, paint)
        
        y += 40f
        paint.textSize = 12f
        paint.isFakeBoldText = false
        canvas.drawText("Date: ${java.text.SimpleDateFormat("dd/MM/yyyy").format(java.util.Date())}", 20f, y, paint)
        
        y += 40f
        paint.isFakeBoldText = true
        canvas.drawText("Metric", 20f, y, paint)
        canvas.drawText("Revenue", 150f, y, paint)
        canvas.drawText("Progress", 230f, y, paint)
        
        y += 10f
        canvas.drawLine(20f, y, 280f, y, paint)
        
        y += 30f
        paint.isFakeBoldText = false
        val metrics = listOf(
            Triple("Daily", state.dailyRevenue, state.dailyProgress),
            Triple("Weekly", state.weeklyRevenue, state.weeklyProgress),
            Triple("Monthly", state.monthlyRevenue, state.monthlyProgress),
            Triple("Yearly", state.yearlyRevenue, state.yearlyProgress)
        )
        
        metrics.forEach { (label, rev, prog) ->
            canvas.drawText(label, 20f, y, paint)
            canvas.drawText("₹${rev.toInt()}", 150f, y, paint)
            canvas.drawText("${prog.toInt()}%", 230f, y, paint)
            y += 30f
        }

        pdfDocument.finishPage(page)
        savePdfToDownloads(context, pdfDocument, "Analytics_Summary.pdf")
    }

    private fun savePdfToDownloads(context: Context, pdfDocument: PdfDocument, fileName: String) {
        val resolver = context.contentResolver
        val contentValues = android.content.ContentValues().apply {
            put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                put(android.provider.MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
        }

        val uri = resolver.insert(android.provider.MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        
        try {
            uri?.let {
                resolver.openOutputStream(it)?.use { outputStream ->
                    pdfDocument.writeTo(outputStream)
                }
                NotificationHelper.showDownloadNotification(context, fileName)
                Toast.makeText(context, "PDF saved to Downloads", Toast.LENGTH_LONG).show()
            } ?: run {
                // Fallback for older versions if URI insertion fails or not supported correctly
                val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
                pdfDocument.writeTo(FileOutputStream(file))
                NotificationHelper.showDownloadNotification(context, fileName)
                Toast.makeText(context, "PDF saved to Downloads", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            pdfDocument.close()
        }
    }
}
