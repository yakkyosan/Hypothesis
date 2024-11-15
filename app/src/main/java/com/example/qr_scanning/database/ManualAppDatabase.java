package com.example.qr_scanning.database;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory;

import com.example.qr_scanning.dao.*;

public class ManualAppDatabase extends AppDatabase {

    private final UserDao userDao;
    private final ItemDao itemDao;
    private final SupportSQLiteOpenHelper openHelper;

    public ManualAppDatabase(Context context) {
        SupportSQLiteOpenHelper.Configuration configuration = SupportSQLiteOpenHelper.Configuration.builder(context)
                .name("app_database.db")
                .callback(new SupportSQLiteOpenHelper.Callback(1) {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        // テーブルの作成
                        db.execSQL("CREATE TABLE IF NOT EXISTS user_table (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, profileImageUrl TEXT, points INTEGER)");
                        db.execSQL("CREATE TABLE IF NOT EXISTS item_table (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, requiredPoints INTEGER, imageResId INTEGER)");
                    }

                    @Override
                    public void onUpgrade(@NonNull SupportSQLiteDatabase db, int oldVersion, int newVersion) {
                        // マイグレーション処理（ここでは単純にデータベースをリセット）
                        db.execSQL("DROP TABLE IF EXISTS user_table");
                        db.execSQL("DROP TABLE IF EXISTS item_table");
                        onCreate(db);
                    }
                })
                .build();

        openHelper = new FrameworkSQLiteOpenHelperFactory().create(configuration);

        // DAOのインスタンスを生成
        userDao = new ManualUserDao(openHelper);
        itemDao = new ManualItemDao(openHelper);
    }

    @Override
    public UserDao userDao() {
        return userDao;
    }

    @Override
    public ItemDao itemDao() {
        return itemDao;
    }

    @NonNull
    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
        return openHelper;
    }

    @SuppressLint("RestrictedApi")
    @NonNull
    @Override
    protected InvalidationTracker createInvalidationTracker() {
        // InvalidationTrackerは使用しない場合、ダミーで返す
        return new InvalidationTracker(this, "user_table", "item_table");
    }

    @Override
    public void clearAllTables() {
        openHelper.getWritableDatabase().execSQL("DELETE FROM user_table");
        openHelper.getWritableDatabase().execSQL("DELETE FROM item_table");
    }
}
