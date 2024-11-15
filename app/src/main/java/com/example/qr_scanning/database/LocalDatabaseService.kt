package com.example.qr_scanning.database

// ローカルデータ保存用Database
// AppDatabaseの操作を抽象化する

import android.content.Context
import com.example.qr_scanning.model.*

class LocalDatabaseService(context: Context) {

    // AppDatabaseのインスタンスを取得
    private val db: AppDatabase = AppDatabase.getInstance(context)

    // ユーザー情報を取得するメソッド
    fun getUser(userId: Int): User? {
        return db.userDao().getUserById(userId)
    }

    // ユーザー情報を追加するメソッド
    fun insertUser(user: User) {
        db.userDao().insert(user)
    }

    // ユーザー情報を更新するメソッド
    fun updateUser(user: User) {
        db.userDao().update(user)
    }

    // すべてのアイテムを取得するメソッド
    fun getAllItems(): List<Item> {
        return db.itemDao().getAllItems()
    }

    // アイテムを追加するメソッド
    fun insertItem(item: Item) {
        db.itemDao().insert(item)
    }

    // その他必要なメソッドをここに追加
}

