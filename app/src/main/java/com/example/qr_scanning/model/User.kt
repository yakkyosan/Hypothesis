package com.example.qr_scanning.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// ユーザーの情報を保持するデータクラス
@Entity(tableName = "user_table")
data class User(
        @PrimaryKey(autoGenerate = true)
        val id: Int = 0,                 // ユーザーの一意なID
        val name: String,                // ユーザー名
        val profileImageUrl: String?,     // プロフィール画像のURL
        var points: Int                  // ユーザーの保有ポイント
)
