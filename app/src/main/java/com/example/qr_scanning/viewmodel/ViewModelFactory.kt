// ViewModelFactory.kt
package com.example.qr_scanning.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.qr_scanning.repository.ItemRepository
import com.example.qr_scanning.repository.QrCodeRepository
import com.example.qr_scanning.repository.UserRepository

class ViewModelFactory(
    private val userRepository: UserRepository,
    private val qrCodeRepository: QrCodeRepository? = null,
    private val itemRepository: ItemRepository? = null,
    private val validQrCodeIds: List<String>? = null
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(RewardViewModel::class.java) -> {
                if (itemRepository == null) {
                    throw IllegalArgumentException("ItemRepository must not be null for RewardViewModel")
                }
                RewardViewModel(userRepository, itemRepository) as T
            }
            modelClass.isAssignableFrom(QrViewModel::class.java) -> {
                if (qrCodeRepository == null) {
                    throw IllegalArgumentException("QrCodeRepository must not be null for QrViewModel")
                }
                QrViewModel(userRepository, qrCodeRepository, validQrCodeIds!!) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
