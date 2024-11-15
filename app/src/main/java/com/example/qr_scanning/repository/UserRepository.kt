package com.example.qr_scanning.repository

// ユーザー関連のデータ操作
// LocalDatabaseServiceを使い、UIからアクセスしない

import com.example.qr_scanning.model.User
import com.example.qr_scanning.database.LocalDatabaseService

class UserRepository(private val localDatabaseService: LocalDatabaseService) {

    // ユーザー情報を取得する
    fun getUser(userId: Int): User? {
        return localDatabaseService.getUser(userId)
    }

    // ユーザー情報を追加する
    fun insertUser(user: User) {
        localDatabaseService.insertUser(user)
    }

    // ユーザー情報を更新する
    fun updateUser(user: User) {
        localDatabaseService.updateUser(user)
    }

    // その他のデータ操作メソッドを追加
}
