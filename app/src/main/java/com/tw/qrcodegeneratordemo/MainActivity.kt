package com.tw.qrcodegeneratordemo

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.tw.qrcodegeneratordemo.databinding.ActivityMainBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var qrBitmap: Bitmap? = null
    val TAG:String = "MainActivity"
    private val QR_SIZE = 1024

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.generateQrBtn.setOnClickListener {
            generateQrCode()
        }

    }

    private fun generateQrCode() {
        val inputText = binding.etTextToConvert.text.toString()
        try {
            val encoder = BarcodeEncoder()
            qrBitmap = encoder.encodeBitmap(inputText, BarcodeFormat.QR_CODE, QR_SIZE, QR_SIZE)
            binding.ivQrCode.setImageBitmap(qrBitmap)


            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_STREAM, getUriFromBitmap())
                putExtra(Intent.EXTRA_TEXT, "Scan this code")
                type = "image/*"
            }
            startActivity(Intent.createChooser(shareIntent, "Share via"))

        } catch (e: WriterException) {
            Log.e(TAG, "generateQrCode: ${e.message}")
        }
    }

    private fun getUriFromBitmap(): Uri? {
        try {

            val file = File(
                this.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "qr_code_${System.currentTimeMillis()}.png"
            )
            val outStream = FileOutputStream(file)
            qrBitmap?.compress(Bitmap.CompressFormat.PNG, 90, outStream)
            return FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file)
        } catch (e: IOException) {
            Log.e(TAG, "getUriFromBitmap: ${e.message}")
        }
        return null
    }

}