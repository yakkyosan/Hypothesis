package com.example.qr_scanning.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.example.qr_scanning.dao.ItemDao;
import com.example.qr_scanning.dao.UserDao;
import com.example.qr_scanning.model.Item;
import com.example.qr_scanning.model.User;

// Roomデータベースの抽象クラス
@Database(entities = {User.class, Item.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserDao userDao();
    public abstract ItemDao itemDao();

    // 手動でインスタンスを提供
    private static AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = new ManualAppDatabase(context);
        }
        return instance;
    }
}
