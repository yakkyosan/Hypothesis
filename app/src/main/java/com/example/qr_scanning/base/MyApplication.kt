package com.example.qr_scanning.base

import android.app.Application
import android.content.res.TypedArray
import com.example.qr_scanning.R
import com.example.qr_scanning.database.LocalDatabaseService
import com.example.qr_scanning.model.Item
import com.example.qr_scanning.repository.ItemRepository
import com.example.qr_scanning.repository.UserRepository

class MyApplication : Application() {

    // リポジトリの共有インスタンス
    lateinit var userRepository: UserRepository
    lateinit var itemRepository: ItemRepository

    override fun onCreate() {
        super.onCreate()

        // ローカルデータベースの初期化
        val localDatabaseService = LocalDatabaseService(this)

        // リポジトリの初期化
        userRepository = UserRepository(localDatabaseService)
        itemRepository = ItemRepository(localDatabaseService)

        // アイテムデータの初期化
        initializeItems(localDatabaseService)
    }

    private fun initializeItems(localDatabaseService: LocalDatabaseService) {
        val itemDao = localDatabaseService.getItemDao()

        // データベースにデータがない場合のみ初期データを追加
        if (itemDao.getAllItems().isEmpty()) {
            val ids: IntArray = resources.getIntArray(R.array.item_ids)
            val names: Array<String> = resources.getStringArray(R.array.item_names)
            val points: IntArray = resources.getIntArray(R.array.item_points)
            val images: TypedArray = resources.obtainTypedArray(R.array.item_images)

            for (i in ids.indices) {
                val item = Item(
                    ids[i],
                    names[i],
                    points[i],
                    images.getResourceId(i, 0)
                )
                itemDao.insert(item)
            }
            images.recycle() // TypedArrayのリソースを解放
        }
    }
}
