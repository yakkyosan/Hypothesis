// QrCodeDao.java
package com.example.qr_scanning.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.qr_scanning.model.ScannedQrCode;

@Dao
public interface QrCodeDao {

    @Insert
    void insert(ScannedQrCode scannedQrCode);

    @Query("SELECT EXISTS(SELECT 1 FROM scanned_qr_code_table WHERE qrCode = :qrCode LIMIT 1)")
    boolean isQrCodeScanned(String qrCode);
}
