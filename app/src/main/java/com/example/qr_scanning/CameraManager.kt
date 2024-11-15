package com.example.qr_scanning

import android.content.Context
import android.util.Log
import android.os.Bundle
import android.widget.Button
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraManager(
    private val context: Context,
    private val previewView: PreviewView,
    private val allowedQRCodes: List<String>,
    private val onQRCodeScanned: (String) -> Unit
) {

    private lateinit var cameraExecutor: ExecutorService
    private val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

    init {
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    @androidx.annotation.OptIn(ExperimentalGetImage::class)
    fun startCamera() {
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor) { imageProxy ->
                        processImage(imageProxy)
                    }
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    context as androidx.lifecycle.LifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalyzer
                )
            } catch (exc: Exception) {
                Log.e("CameraManager", "Camera binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    @androidx.annotation.OptIn(ExperimentalGetImage::class)
    private fun processImage(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(
                mediaImage,
                imageProxy.imageInfo.rotationDegrees
            )
            scanBarcodes(image, imageProxy)
        }
    }

    private fun scanBarcodes(image: InputImage, imageProxy: ImageProxy) {
        val scanner = BarcodeScanning.getClient()
        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    val scannedData = when (barcode.valueType) {
                        Barcode.TYPE_URL -> barcode.url?.url
                        Barcode.TYPE_TEXT -> barcode.displayValue
                        else -> null
                    }
                    if (scannedData != null && allowedQRCodes.contains(scannedData)) {
                        onQRCodeScanned(scannedData)
                    }
                }
            }
            .addOnFailureListener {
                Log.e("CameraManager", "Barcode scanning failed", it)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }

    fun shutdown() {
        cameraExecutor.shutdown()
    }
}

class CameraActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera_activity)

        // 戻るボタンの設定
        val backButton = findViewById<Button>(R.id.backButton)
        backButton.setOnClickListener {
            finish() // アクティビティを終了して前の画面に戻る
        }

        // ここでカメラの初期化や起動のコードを追加
    }
}
