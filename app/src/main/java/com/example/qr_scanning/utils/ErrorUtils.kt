package com.example.qr_scanning.utils

import android.content.Context
import android.util.Log
import android.widget.Toast

object ErrorUtils {

    /**
     * エラーログを記録する
     * @param tag: タグ
     * @param message: メッセージ
     * @param throwable: 例外
     */
    fun logError(tag: String, message: String, throwable: Throwable? = null) {
        Log.e(tag, message, throwable)
    }

    /**
     * エラーメッセージをユーザーに表示する
     * @param context: コンテキスト
     * @param message: メッセージ
     */
    fun showError(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}
