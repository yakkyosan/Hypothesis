// QrViewModel.kt
package com.example.qr_scanning.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.qr_scanning.repository.QrCodeRepository
import com.example.qr_scanning.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class QrViewModel(
    private val userRepository: UserRepository,
    private val qrCodeRepository: QrCodeRepository,
    private val validQrCodeIds: List<String> // 有効なQRコードのIDリスト
) : ViewModel() {

    private val _scanResult = MutableLiveData<String?>()
    val scanResult: LiveData<String?> get() = _scanResult

    /**
     * QRコードの内容を検証し、ポイントを付与する処理
     * @param qrCode: 検出されたQRコードの内容
     */
    fun validateAndAwardPoints(qrCode: String) {
        CoroutineScope(Dispatchers.IO).launch {
            if (isValidQrCode(qrCode)) {
                if (!qrCodeRepository.isQrCodeScanned(qrCode)) {
                    qrCodeRepository.insertScannedQrCode(qrCode)
                    addPoints(20)
                    _scanResult.postValue("20ポイント獲得しました！")
                } else {
                    _scanResult.postValue("このQRコードは既に使用されています。")
                }
            } else {
                _scanResult.postValue("無効なQRコードです。")
            }
        }
    }

    /**
     * QRコードが有効かどうかを判断する
     * @param qrCode: 検出されたQRコードの内容
     * @return 有効であれば true、無効であれば false
     */
    private fun isValidQrCode(qrCode: String): Boolean {
        return validQrCodeIds.contains(qrCode)
    }

    /**
     * ユーザーにポイントを追加する
     * @param points: 追加するポイント数
     */
    private fun addPoints(points: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val user = userRepository.getUser(1) // ユーザーID 1 のユーザーデータを取得
            user?.let {
                it.points += points
                userRepository.updateUser(it)
                // ポイント追加の成功メッセージは validateAndAwardPoints() で設定
            } ?: run {
                // ユーザーが存在しない場合のエラーメッセージ
                _scanResult.postValue("ユーザーが見つかりません。")
            }
        }
    }

    /**
     * スキャン結果をクリアする
     */
    fun clearScanResult() {
        _scanResult.value = null
    }
}
