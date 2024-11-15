package com.example.qr_scanning.model;

// ユーザーの[名前, プロフ画像, 獲得ポイント]情報
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_table")
public class User {

    @PrimaryKey(autoGenerate = true)
    private int id; // ユーザーの一意なID

    private String name; // ユーザー名

    private String profileImageUrl; // プロフィール画像のURL

    private int points; // ユーザーの保有ポイント

    // コンストラクタ
    public User(String name, String profileImageUrl, int points) {
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.points = points;
    }

    // ゲッターとセッター
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
