@file:Suppress("DEPRECATION")

package com.example.qr_scanning.utils

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.example.qr_scanning.R
import java.lang.ref.WeakReference

object DialogUtils {

    private var loadingDialogRef: WeakReference<AlertDialog>? = null

    /**
     * アラートダイアログを表示する
     * @param context: コンテキスト
     * @param title: タイトル
     * @param message: メッセージ
     * @param onOk: OKボタンが押された際のコールバック
     */
    fun showAlert(context: Context, title: String, message: String, onOk: () -> Unit = {}) {
        // Activity が有効か確認
        if (context !is AppCompatActivity || context.isFinishing || context.isDestroyed) {
            return
        }

        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                onOk()
            }
            .setCancelable(false)
            .show()
    }

    /**
     * ローディングダイアログを表示する
     * @param context: コンテキスト
     * @param message: メッセージ
     */
    fun showLoadingDialog(context: Context, message: String) {
        // Activity が有効か確認
        if (context !is AppCompatActivity || context.isFinishing || context.isDestroyed) {
            return
        }

        dismissLoadingDialog() // 既存のダイアログを閉じる

        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.dialog_loading, null)
        builder.setView(dialogView)
        builder.setMessage(message)
        builder.setCancelable(false)
        val loadingDialog = builder.create()
        loadingDialog.show()
        loadingDialogRef = WeakReference(loadingDialog)
    }

    /**
     * ローディングダイアログを非表示にする
     */
    fun dismissLoadingDialog() {
        loadingDialogRef?.get()?.dismiss()
        loadingDialogRef = null
    }
}
