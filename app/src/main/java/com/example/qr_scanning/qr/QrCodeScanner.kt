// QrCodeScanner.kt
package com.example.qr_scanning.qr

import android.content.Context
import android.util.Log
import android.util.Size
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.example.qr_scanning.utils.ErrorUtils
import com.example.qr_scanning.viewmodel.QrViewModel
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class QrCodeScanner(
    private val context: Context,
    private val previewView: PreviewView,
    private val onQrCodeDetected: (String) -> Unit,
    private val qrViewModel: QrViewModel
) {
    private var cameraProvider: ProcessCameraProvider? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    // 検出の中断フラグ
    @Volatile
    private var isProcessing = false

    fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            val imageAnalyzer = ImageAnalysis.Builder()
                .setTargetResolution(Size(1280, 720))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
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
                cameraProvider?.unbindAll()
                cameraProvider?.bindToLifecycle(
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

    @Suppress("OPT_IN_ARGUMENT_IS_NOT_MARKER")
    @androidx.annotation.OptIn(ExperimentalGetImage::class)
    @OptIn(ExperimentalGetImage::class)
    private suspend fun processImageProxy(imageProxy: ImageProxy) {
        if (isProcessing) {
            imageProxy.close()
            return
        }

        isProcessing = true // フラグを設定

        try {
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image =
                    InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                val options = BarcodeScannerOptions.Builder()
                    .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                    .build()
                val scanner = BarcodeScanning.getClient(options)

                // QRコード解析
                val barcodes = withContext(Dispatchers.Default) {
                    scanner.process(image).await()
                }

                for (barcode in barcodes) {
                    if (barcode.rawValue != null) {
                        val qrCode = barcode.rawValue!!
                        Log.d("QrCodeScanner", "QRコード検出：$qrCode")
                        onQrCodeDetected(qrCode)
                        qrViewModel.validateAndAwardPoints(qrCode)
                        break // 最初のQRコードのみ処理
                    }
                }
            }
        } catch (e: Exception) {
            ErrorUtils.logError("QrCodeScanner", "QRコードの解析に失敗しました", e)
        } finally {
            imageProxy.close() // リソースを解放

            // 一定時間後に検出再開
            coroutineScope.launch {
                kotlinx.coroutines.delay(2000) // 2秒待機
                isProcessing = false
            }
        }
    }

    fun stopCamera() {
        cameraProvider?.unbindAll()
    }
}
