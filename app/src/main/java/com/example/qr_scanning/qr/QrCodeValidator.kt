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
        if (isValidQrCode(qrCode)) {
            // 有効なQRコードの場合、20ポイントを加算
            qrViewModel.addPoints(20)
        } else {
            // 無効なQRコードの場合、ログまたは通知を表示する
            qrViewModel.updateScanResult("無効なQRコードです")
        }
    }

    /**
     * QRコードが有効かどうかを判断する
     * @param qrCode: 検出されたQRコードの内容
     * @return 有効であれば true、無効であれば false
     */
    fun isValidQrCode(qrCode: String): Boolean {
        // シンプルに、QRコードが空でない場合を有効とする
        // 必要に応じて更に詳細な検証を実装
        return qrCode.isNotBlank()
    }
}
