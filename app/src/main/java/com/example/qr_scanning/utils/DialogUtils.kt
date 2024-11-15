package com.example.qr_scanning.utils

// 確認ダイアログのためのユーティリティ関数

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog

object DialogUtils {

    // 確認ダイアログを表示するメソッド
    fun showConfirmationDialog(
        context: Context,
        title: String,
        message: String,
        positiveButtonText: String = "OK",
        negativeButtonText: String = "キャンセル",
        onConfirmed: () -> Unit
    ) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButtonText) { _, _ -> onConfirmed() }
            .setNegativeButton(negativeButtonText, null)
            .show()
    }

    // 通知ダイアログを表示するメソッド
    fun showAlert(
        context: Context,
        title: String,
        message: String,
        buttonText: String = "OK"
    ) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(buttonText, null)
            .show()
    }
}
