package com.example.qr_scanning.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
import com.example.qr_scanning.model.Item;

@Dao
public interface ItemDao {

    // アイテムを追加するメソッド
    @Insert
    void insert(Item item);

    // 全てのアイテムを取得するメソッド
    @Query("SELECT * FROM item_table")
    List<Item> getAllItems();

    // 特定のIDでアイテムを取得するメソッド
    @Query("SELECT * FROM item_table WHERE id = :itemId")
    Item getItemById(int itemId);

    @Update
    void update(Item item);
}
