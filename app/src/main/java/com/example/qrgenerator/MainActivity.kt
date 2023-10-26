package com.example.qrgenerator

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Media
import android.view.WindowManager
import androidx.core.graphics.drawable.toBitmap
import com.example.qrgenerator.databinding.ActivityMainBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //when its in full screen
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        //full screen
        binding.apply {
            //generating the qr code
            btnGenerate.setOnClickListener{
                val text = etText.text.toString().trim()
                if (text.isNotEmpty()){
                    val bitmap = generateQr(text)
                    tvQrCode.setImageBitmap(bitmap)
                    btnShare.isEnabled = true
                }
            }
            //share
            btnShare.setOnClickListener {
                shareQrCode()
            }
        }
    }
    private fun generateQr(text:String):Bitmap{
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 512, 512)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width){
            for (y in 0 until height){
                bitmap.setPixel(x,y,if(bitMatrix[x,y])Color.BLACK else Color.WHITE)
            }
        }
        return bitmap
    }
    private fun shareQrCode(){
        val bitmap = (binding.tvQrCode.drawable).toBitmap()
        val uri = getImageUri(bitmap)
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_STREAM,uri)
        startActivity(Intent.createChooser(intent, "share QrCode"))

    }
    private fun getImageUri(bitmap: Bitmap): Uri {
        val path = MediaStore.Images.Media.insertImage(contentResolver,bitmap, "Qr Code", null)
        return Uri.parse(path)

    }
}