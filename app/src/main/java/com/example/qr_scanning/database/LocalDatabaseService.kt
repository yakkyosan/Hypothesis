// LocalDatabaseService.kt
package com.example.qr_scanning.database

import android.content.Context
import com.example.qr_scanning.model.Item
import com.example.qr_scanning.model.User
import com.example.qr_scanning.repository.ItemRepository
import com.example.qr_scanning.repository.QrCodeRepository
import com.example.qr_scanning.repository.UserRepository

class LocalDatabaseService(context: Context) {

    // AppDatabaseのインスタンスを取得
    private val db: AppDatabase = AppDatabase.getInstance(context)

    // リポジトリの初期化
    val userRepository: UserRepository = UserRepository(db.userDao())
    val itemRepository: ItemRepository = ItemRepository(db.itemDao())
    val qrCodeRepository: QrCodeRepository = QrCodeRepository(db.qrCodeDao())

    // ユーザー情報を取得するメソッド
    suspend fun getUser(userId: Int): User? {
        return db.userDao().getUserById(userId)
    }

    // ユーザー情報を追加するメソッド
    suspend fun insertUser(user: User) {
        db.userDao().insert(user)
    }

    // ユーザー情報を更新するメソッド
    suspend fun updateUser(user: User) {
        db.userDao().update(user)
    }

    // すべてのアイテムを取得するメソッド
    suspend fun getAllItems(): List<Item> {
        return db.itemDao().getAllItems()
    }

    // アイテムを追加するメソッド
    suspend fun insertItem(item: Item) {
        db.itemDao().insert(item)
    }
}
