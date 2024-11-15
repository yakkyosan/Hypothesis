package com.example.qr_scanning.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qr_scanning.model.Item
import com.example.qr_scanning.repository.ItemRepository
import com.example.qr_scanning.repository.UserRepository
import kotlinx.coroutines.launch

class RewardViewModel(
    private val userRepository: UserRepository,
    private val itemRepository: ItemRepository
) : ViewModel() {

    private val _itemList = MutableLiveData<List<Item>>()
    val itemList: LiveData<List<Item>> get() = _itemList

    private val _exchangeMessage = MutableLiveData<String>()
    val exchangeMessage: LiveData<String> get() = _exchangeMessage

    init {
        loadItems()
    }

    private fun loadItems() {
        viewModelScope.launch {
            val items = itemRepository.getAllItems() // アイテム一覧を取得
            _itemList.postValue(items)
        }
    }

    fun exchangeItem(itemId: Int) {
        viewModelScope.launch {
            val item = itemRepository.getItemById(itemId) // アイテム取得
            val user = userRepository.getUser(1) // 仮にID 1のユーザー

            if (item != null && user != null) {
                if (user.points >= item.requiredPoints) {
                    // ポイントを消費して交換
                    val updatedUser = user.copy(points = user.points - item.requiredPoints)
                    userRepository.updateUser(updatedUser)
                    _exchangeMessage.postValue("${item.name} を交換しました！")
                } else {
                    // ポイント不足のメッセージ
                    _exchangeMessage.postValue("ポイントが足りません！")
                }
            } else {
                _exchangeMessage.postValue("交換に失敗しました。")
            }
        }
    }
}
