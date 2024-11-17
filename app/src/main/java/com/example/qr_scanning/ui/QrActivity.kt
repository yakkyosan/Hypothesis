// QrActivity.kt
package com.example.qr_scanning.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.qr_scanning.R
import com.example.qr_scanning.base.MyApplication
import com.example.qr_scanning.databinding.ActivityQrBinding
import com.example.qr_scanning.utils.DialogUtils
import com.example.qr_scanning.utils.ErrorUtils
import com.example.qr_scanning.utils.PermissionUtils
import com.example.qr_scanning.viewmodel.QrViewModel
import com.example.qr_scanning.viewmodel.ViewModelFactory
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class QrActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQrBinding
    private lateinit var qrViewModel: QrViewModel

    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService

    companion object {
        private const val TAG = "QrActivity"
        private const val CAMERA_PERMISSION_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // LocalDatabaseServiceの初期化
        val myApplication = application as MyApplication
        val localDatabaseService = myApplication.localDatabaseService

        // リポジトリの取得
        val userRepository = localDatabaseService.userRepository
        val qrCodeRepository = localDatabaseService.qrCodeRepository

        // 有効なQRコードのIDリストを取得
        val validQrCodeIds = resources.getStringArray(R.array.valid_qr_code_ids).toList()

        // ViewModelFactoryの初期化
        val factory = ViewModelFactory(
            userRepository = userRepository,
            qrCodeRepository = qrCodeRepository,
            validQrCodeIds = validQrCodeIds // IDリスト
        )
        qrViewModel = ViewModelProvider(this, factory).get(QrViewModel::class.java)

        // ViewModelのscanResultを監視
        qrViewModel.scanResult.observe(this) { message ->
            message?.let {
                DialogUtils.dismissLoadingDialog()
                when (it) {
                    "20ポイント獲得しました！" -> {
                        DialogUtils.showAlert(this, "成功", it) {
                            // ダイアログが閉じられた後にカメラを再開
                            startCamera()
                        }
                    }
                    "このQRコードは既に使用されています。" -> {
                        DialogUtils.showAlert(this, "警告", it) {
                            startCamera()
                        }
                    }
                    "無効なQRコードです。" -> {
                        DialogUtils.showAlert(this, "エラー", it) {
                            startCamera()
                        }
                    }
                    "ユーザーが見つかりません。" -> {
                        DialogUtils.showAlert(this, "エラー", it) {
                            startCamera()
                        }
                    }
                    else -> {
                        DialogUtils.showAlert(this, "情報", it) {
                            startCamera()
                        }
                    }
                }
                qrViewModel.clearScanResult()
            }
        }

        // カメラのパーミッションを確認
        if (PermissionUtils.isCameraPermissionGranted(this)) {
            startCamera()
        } else {
            PermissionUtils.requestCameraPermission(this)
        }

        // カメラの実行エグゼキューターを初期化
        cameraExecutor = Executors.newSingleThreadExecutor()

        // シャッターボタンのクリックリスナーを設定
        binding.btnCapture.setOnClickListener {
            takePhoto()
        }

        // 戻るボタンの処理
        binding.btnBackToMain.setOnClickListener {
            finish()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // CameraProviderの取得
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Previewの設定
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.previewView.surfaceProvider)
                }

            // ImageCaptureの設定
            imageCapture = ImageCapture.Builder()
                .build()

            // カメラの選択（背面カメラ）
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // 既存のバインドを解除
                cameraProvider.unbindAll()

                // カメラのバインド
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )

            } catch (exc: Exception) {
                ErrorUtils.logError(TAG, "カメラの起動に失敗しました", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        // ImageCaptureが存在しない場合は何もしない
        val imageCapture = imageCapture ?: return

        // Activityが有効か確認
        if (isFinishing || isDestroyed) {
            return
        }

        // ローディングダイアログを表示
        DialogUtils.showLoadingDialog(this, "解析中...")

        // 画像をキャプチャ
        imageCapture.takePicture(ContextCompat.getMainExecutor(this), object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(imageProxy: ImageProxy) {
                super.onCaptureSuccess(imageProxy)
                processImage(imageProxy)
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                ErrorUtils.logError(TAG, "画像のキャプチャに失敗しました", exception)
                DialogUtils.dismissLoadingDialog()
                ErrorUtils.showError(this@QrActivity, "画像のキャプチャに失敗しました")

                // カメラの再開（画像を読み込めなかったとき）
                startCamera()
            }
        })
    }

    @OptIn(ExperimentalGetImage::class)
    private fun processImage(imageProxy: ImageProxy) {

        // カメラの停止
        val cameraProvider = ProcessCameraProvider.getInstance(this).get()
        cameraProvider.unbindAll()

        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            val scanner = BarcodeScanning.getClient()

            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    var qrCodeDetected = false
                    for (barcode in barcodes) {
                        if (barcode.rawValue != null) {
                            val qrCode = barcode.rawValue!!
                            Log.d(TAG, "QRコード検出：$qrCode")
                            qrCodeDetected = true
                            // QRコードの検証とポイント付与
                            qrViewModel.validateAndAwardPoints(qrCode)
                            break // 最初のQRコードのみ処理
                        }
                    }
                    if (!qrCodeDetected) {
                        DialogUtils.dismissLoadingDialog()
                        DialogUtils.showAlert(this, "エラー", "QRコードが検出されませんでした") {
                            startCamera()
                        }
                    }
                }
                .addOnFailureListener { e ->
                    ErrorUtils.logError(TAG, "QRコードの解析に失敗しました", e)
                    DialogUtils.dismissLoadingDialog()
                    DialogUtils.showAlert(this, "エラー", "QRコードの解析に失敗しました") {
                        startCamera()
                    }
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
            DialogUtils.dismissLoadingDialog()
            DialogUtils.showAlert(this, "エラー", "画像の取得に失敗しました") {
                startCamera()
            }
        }
    }

    // パーミッションリクエスト結果の処理
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == PermissionUtils.CAMERA_REQUEST_CODE) {
            if (PermissionUtils.isCameraPermissionGranted(this)) {
                startCamera()
            } else {
                ErrorUtils.showError(this, "カメラ権限が必要です")
                finish()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()

        DialogUtils.dismissLoadingDialog()
    }
}
