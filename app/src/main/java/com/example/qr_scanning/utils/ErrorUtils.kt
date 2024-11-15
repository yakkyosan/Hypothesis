package com.example.qr_scanning.utils

// エラーハンドリング用ユーティリティ

import android.content.Context
import android.util.Log
import android.widget.Toast

object ErrorUtils {

    // エラーメッセージを表示するメソッド
    fun showError(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    // ログにエラー情報を記録するメソッド
    fun logError(tag: String, message: String, throwable: Throwable? = null) {
        Log.e(tag, message, throwable)
    }

    // デフォルトのエラーメッセージを表示するメソッド
    fun showDefaultError(context: Context) {
        showError(context, "エラーが発生しました。再度お試しください。")
    }
}
