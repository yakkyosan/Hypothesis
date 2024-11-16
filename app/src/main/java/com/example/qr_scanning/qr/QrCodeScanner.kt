package com.example.qr_scanning.qr

// QRコードをCamaraXで読み取る

import android.content.Context
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.Preview
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
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
    private val onQrCodeDetected: (String) -> Unit,
    private val qrCodeValidator: QrCodeValidator
) {
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    // 検出の中断フラグ
    private var isProcessing = false

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
                    if (!isProcessing) {
                        coroutineScope.launch {
                            processImageProxy(imageProxy)
                        }
                    } else {
                        imageProxy.close()
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
    private suspend fun processImageProxy(imageProxy: ImageProxy) {
        if (isProcessing) {
            imageProxy.close()
            return
        }

        isProcessing = true // フラグを最初に設定

        try {
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image =
                    InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                val scanner = BarcodeScanning.getClient()

                // QRコード解析
                val barcodes = withContext(Dispatchers.Default) {
                    scanner.process(image).await()
                }

                for (barcode in barcodes) {
                    if (barcode.rawValue != null) {
                        val qrCode = barcode.rawValue!!
                        onQrCodeDetected(qrCode)
                        qrCodeValidator.validateAndAwardPoints(qrCode)
                        break
                    }
                }
            }
        } catch (e: Exception) {
            ErrorUtils.logError("QrCodeScanner", "QRコードの解析に失敗しました", e)
        } finally {
            imageProxy.close() // 必ずリソースを解放
            coroutineScope.launch {
                kotlinx.coroutines.delay(2000) // 遅延後にフラグを解除
                isProcessing = false
            }
        }
    }
}