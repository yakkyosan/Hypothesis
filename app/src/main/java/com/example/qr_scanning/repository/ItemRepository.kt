package com.example.qr_scanning.repository

import com.example.qr_scanning.database.LocalDatabaseService
import com.example.qr_scanning.model.Item

// 商品関連のデータ操作


class ItemRepository(private val localDatabaseService: LocalDatabaseService) {

    // すべてのアイテムを取得するメソッド
    fun getAllItems(): List<Item> {
        return localDatabaseService.getAllItems()
    }

    // 特定のアイテムをIDで取得するメソッド
    fun getItemById(itemId: Int): Item? {
        return localDatabaseService.getAllItems().find { it.id == itemId }
    }

    // 新しいアイテムを追加するメソッド
    fun insertItem(item: Item) {
        localDatabaseService.insertItem(item)
    }
}
