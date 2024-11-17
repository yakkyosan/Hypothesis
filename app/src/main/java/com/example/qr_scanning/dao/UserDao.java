package com.example.qr_scanning.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.qr_scanning.model.User;

@Dao
public interface UserDao {

    // ユーザーを追加するメソッド
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User user);

    // 特定のIDでユーザー情報を取得するメソッド
    @Query("SELECT * FROM user_table WHERE id = :userId")
    User getUserById(int userId);

    // ユーザー情報を更新するメソッド
    @Update
    void update(User user);
}
