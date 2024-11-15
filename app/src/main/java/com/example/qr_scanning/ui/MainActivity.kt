package com.example.qr_scanning.ui

// ホーム画面です！！
// activity_main.xmlを使います

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.widget.Button
import android.widget.Toast
import androidx.camera.view.PreviewView
import com.example.qr_scanning.CameraManager
import com.example.qr_scanning.R

class MainActivity : AppCompatActivity() {

    private lateinit var cameraManager: CameraManager
    private val allowedQRCodes = listOf(
        "https://example.com/product1",
        "https://example.com/product2",
        "PRODUCT_ID_12345"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // PreviewViewとボタンを取得
        val previewView: PreviewView = findViewById(R.id.previewView)
        val cameraButton: Button = findViewById(R.id.button_camera)

        // CameraManagerの初期化
        cameraManager = CameraManager(
            context = this,
            previewView = previewView,
            allowedQRCodes = allowedQRCodes
        ) { scannedData ->
            handleAllowedQRCode(scannedData)
        }

        // カメラボタンのクリックリスナー
        cameraButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
                cameraManager.startCamera()
            } else {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.CAMERA), 0)
            }
        }
    }

    private fun handleAllowedQRCode(data: String) {
        Toast.makeText(this, "許可されたQRコード: $data", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraManager.shutdown()
    }
}

