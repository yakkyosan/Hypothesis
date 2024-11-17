// QrCodeRepository.kt
package com.example.qr_scanning.repository

import com.example.qr_scanning.dao.QrCodeDao
import com.example.qr_scanning.model.ScannedQrCode

class QrCodeRepository(private val qrCodeDao: QrCodeDao) {

    suspend fun insertScannedQrCode(qrCode: String) {
        val scannedQrCode = ScannedQrCode(qrCode = qrCode)
        qrCodeDao.insert(scannedQrCode)
    }

    suspend fun isQrCodeScanned(qrCode: String): Boolean {
        return qrCodeDao.isQrCodeScanned(qrCode)
    }
}
