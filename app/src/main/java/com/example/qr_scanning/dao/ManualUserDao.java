package com.example.qr_scanning.dao;

import android.database.Cursor;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.example.qr_scanning.model.User;

public class ManualUserDao implements UserDao {

    private final SupportSQLiteOpenHelper openHelper;

    public ManualUserDao(SupportSQLiteOpenHelper openHelper) {
        this.openHelper = openHelper;
    }

    @Override
    public User getUserById(int id) {
        SupportSQLiteDatabase db = openHelper.getReadableDatabase();
        User user = null;
        try (Cursor cursor = db.query("SELECT * FROM user_table WHERE id = ?", new Object[]{id})) {
            if (cursor.moveToFirst()) {
                user = new User(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("profileImageUrl")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("points"))
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public void insert(User user) {
        SupportSQLiteDatabase db = openHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            db.execSQL("INSERT INTO user_table (name, profileImageUrl, points) VALUES (?, ?, ?)",
                    new Object[]{user.getName(), user.getProfileImageUrl(), user.getPoints()});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void update(User user) {
        SupportSQLiteDatabase db = openHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            db.execSQL("UPDATE user_table SET name = ?, profileImageUrl = ?, points = ? WHERE id = ?",
                    new Object[]{user.getName(), user.getProfileImageUrl(), user.getPoints(), user.getId()});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
}
