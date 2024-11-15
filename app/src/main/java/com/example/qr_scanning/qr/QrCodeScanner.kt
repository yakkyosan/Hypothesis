package com.example.qr_scanning.qr

// QRコードをCamaraXで読み取る

import android.content.Context
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.Preview
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

import com.example.qr_scanning.utils.ErrorUtils

class QrCodeScanner(
    private val context: Context,
    private val previewView: PreviewView,
    private val onQrCodeDetected: (String) -> Unit
) {
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            val imageAnalyzer = ImageAnalysis.Builder().build().also {
                it.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
                    coroutineScope.launch {
                        processImageProxy(imageProxy)
                    }
                }
            }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    context as androidx.lifecycle.LifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalyzer
                )
            } catch (e: Exception) {
                ErrorUtils.logError("QrCodeScanner", "カメラの起動に失敗しました", e)
            }

        }, ContextCompat.getMainExecutor(context))
    }

    @OptIn(ExperimentalGetImage::class)
    private suspend fun processImageProxy(imageProxy: androidx.camera.core.ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            val scanner = BarcodeScanning.getClient()

            try {
                // 解析を非同期で実行
                val barcodes = withContext(Dispatchers.Default) {
                    scanner.process(image).await() // ML Kitの解析処理を非同期で待機
                }
                for (barcode in barcodes) {
                    barcode.rawValue?.let { onQrCodeDetected(it) }
                }
            } catch (e: Exception) {
                ErrorUtils.logError("QrCodeScanner", "QRコードの解析に失敗しました", e)
            } finally {
                imageProxy.close()
            }
            }else {
            imageProxy.close()
        }
    }
}
