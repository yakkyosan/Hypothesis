package com.example.qr_scanning.database;

// Roomデータベースの設定と構築

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import com.example.qr_scanning.dao.*;
import com.example.qr_scanning.model.*;


@Database(entities = {User.class, Item.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    // DAOを取得する抽象メソッド
    public abstract UserDao userDao();
    public abstract ItemDao itemDao();

    // シングルトンインスタンス
    private static AppDatabase instance;

    // データベースのインスタンスを取得するメソッド
    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "app_database")
                    .fallbackToDestructiveMigration() // バージョンが異なる場合にデータをリセットする
                    .build();
        }
        return instance;
    }
}
