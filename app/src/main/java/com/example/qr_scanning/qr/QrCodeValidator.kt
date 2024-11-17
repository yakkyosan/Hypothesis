// QrCodeValidator.kt
package com.example.qr_scanning.qr

import com.example.qr_scanning.viewmodel.QrViewModel

class QrCodeValidator(
    private val qrViewModel: QrViewModel
) {
    /**
     * QRコードの内容を検証し、ポイントを付与する処理
     * @param qrCode: 検出されたQRコードの内容
     */
    fun validateAndAwardPoints(qrCode: String) {
        qrViewModel.validateAndAwardPoints(qrCode)
    }
}
