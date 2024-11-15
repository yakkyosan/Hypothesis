package com.example.qr_scanning.ui

// QRコード読み取り画面！！
// activity_qr.xmlを使います

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.example.qr_scanning.databinding.ActivityQrBinding
import com.example.qr_scanning.qr.*
import com.example.qr_scanning.utils.*

class QrActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQrBinding
    private lateinit var qrCodeScanner: QrCodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // カメラのパーミッションを確認
        if (PermissionUtils.isCameraPermissionGranted(this)) {
            initializeScanner()
        } else {
            PermissionUtils.requestCameraPermission(this)
        }

        // 戻るボタンの処理
        binding.btnBackToMain.setOnClickListener {
            finish()
        }
    }

    // カメラの初期化
    private fun initializeScanner() {
        qrCodeScanner = QrCodeScanner(this, binding.previewView, ::onQrCodeDetected)
        qrCodeScanner.startCamera()
    }

    // QRコードが検出されたときの処理
    private fun onQrCodeDetected(qrCode: String) {
        if (QrCodeValidator.isValid(qrCode)) {
            DialogUtils.showAlert(this, "QRコード", "有効なQRコードが読み取られました: $qrCode")
        } else {
            DialogUtils.showAlert(this, "QRコード", "無効なQRコードです。")
        }
    }

    // パーミッションリクエスト結果の処理
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionUtils.handlePermissionResult(
            requestCode, grantResults,
            onGranted = { initializeScanner() },
            onDenied = {
                ErrorUtils.showError(this, "カメラ権限が必要です")
                finish()
            }
        )
    }
}
