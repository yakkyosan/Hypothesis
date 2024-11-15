package com.example.qr_scanning.viewmodel

// QrActivityのデータ管理

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qr_scanning.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class QrViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _scanResult = MutableStateFlow<String?>(null)
    val scanResult: StateFlow<String?> get() = _scanResult

    fun addPoints(points: Int) {
        viewModelScope.launch {
            val user = userRepository.getUser(1)
            user?.let {
                it.points += points
                userRepository.updateUser(it)
                _scanResult.value = "ポイントが追加されました: $points"
            }
        }
    }
}
