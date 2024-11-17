// ScannedQrCode.kt
package com.example.qr_scanning.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scanned_qr_code_table")
data class ScannedQrCode(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val qrCode: String,
    val scannedAt: Long = System.currentTimeMillis()
)
