package com.example.qr_scanning.viewmodel

// RewardActivityのデータ管理

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qr_scanning.repository.ItemRepository
import com.example.qr_scanning.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RewardViewModel(
    private val userRepository: UserRepository,
    private val itemRepository: ItemRepository
) : ViewModel() {

    private val _rewardMessage = MutableStateFlow<String?>(null)
    val rewardMessage: StateFlow<String?> get() = _rewardMessage

    fun exchangeItem(itemId: Int) {
        viewModelScope.launch {
            val user = userRepository.getUser(1)
            val item = itemRepository.getItemById(itemId)

            if (user != null && item != null) {
                if (user.points >= item.requiredPoints) {
                    user.points -= item.requiredPoints
                    userRepository.updateUser(user)
                    _rewardMessage.value = "アイテムと交換しました: ${item.name}"
                } else {
                    _rewardMessage.value = "ポイントが不足しています。"
                }
            }
        }
    }
}
