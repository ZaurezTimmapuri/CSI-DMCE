package com.example.csi_dmce.attendance

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.example.csi_dmce.R
import com.example.csi_dmce.database.Attendance
import com.journeyapps.barcodescanner.CaptureActivity
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions


class scanQrActivity : AppCompatActivity() {
    private lateinit var qrContent: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.scan_qr)

        val sharedPreferences =
            getSharedPreferences("csi_shared_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val btnscan = findViewById<Button>(R.id.button)
        btnscan.setOnClickListener {
            editor.apply {
                putBoolean("csi_shared_prefs", true)
                apply()
            }
            scanQR()
        }
    }

    private fun scanQR() {
        val options = ScanOptions()
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        options.setPrompt("Scan a barcode")
        options.setCameraId(0) // Use a specific camera of the device
        options.setBeepEnabled(false)
        options.setBarcodeImageEnabled(true)
        options.captureActivity = (CaptureAct::class.java)
        barcodeLauncher.launch(ScanOptions())

    }

    private val barcodeLauncher: ActivityResultLauncher<ScanOptions> = registerForActivityResult(
        ScanContract()
    ) { result ->
        if (result.contents == null) {
            Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
        } else {
            qrContent = result.contents
            val intent = Intent(applicationContext, Attendance::class.java)
            intent.putExtra("QR_contents", qrContent )
        }
    }
}

class CaptureAct : CaptureActivity() {
}





