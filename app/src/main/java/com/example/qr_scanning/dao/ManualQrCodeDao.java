// ManualQrCodeDao.java
package com.example.qr_scanning.dao;

import android.database.Cursor;
import android.util.Log;

import androidx.sqlite.db.SupportSQLiteOpenHelper;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.example.qr_scanning.model.ScannedQrCode;

public class ManualQrCodeDao implements QrCodeDao {

    private final SupportSQLiteOpenHelper openHelper;

    public ManualQrCodeDao(SupportSQLiteOpenHelper openHelper) {
        this.openHelper = openHelper;
    }

    @Override
    public void insert(ScannedQrCode scannedQrCode) {
        SupportSQLiteDatabase db = openHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            db.execSQL("INSERT INTO scanned_qr_code_table (qrCode, scannedAt) VALUES (?, ?)",
                    new Object[]{scannedQrCode.getQrCode(), scannedQrCode.getScannedAt()});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public boolean isQrCodeScanned(String qrCode) {
        SupportSQLiteDatabase db = openHelper.getReadableDatabase();

        // テーブル確認ログを追加
        try (Cursor cursor = db.query("SELECT name FROM sqlite_master WHERE type='table' AND name='scanned_qr_code_table'")) {
            if (cursor.moveToFirst()) {
                Log.d("Database", "Table scanned_qr_code_table exists");
            } else {
                Log.e("Database", "Table scanned_qr_code_table does not exist");
            }
        } catch (Exception e) {
            Log.e("Database", "Error checking table existence", e);
        }

        boolean exists = false;
        try (Cursor cursor = db.query("SELECT EXISTS(SELECT 1 FROM scanned_qr_code_table WHERE qrCode = ? LIMIT 1)", new String[]{qrCode})) {
            if (cursor.moveToFirst()) {
                exists = cursor.getInt(0) == 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return exists;
    }

}
