package com.example.qr_scanning.model;

// 交換アイテムの[ID, 名前, 必要ポイント, 画像]情報
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "item_table")
public class Item {

    @PrimaryKey(autoGenerate = true)
    private int id; // アイテムの一意なID

    private String name; // アイテム名

    private int requiredPoints; // 交換に必要なポイント

    private int imageResId; // drawable内のリソースID

    // コンストラクタ
    public Item(int id, String name, int requiredPoints, int imageResId) {
        this.id = id;
        this.name = name;
        this.requiredPoints = requiredPoints;
        this.imageResId = imageResId;
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

    public int getRequiredPoints() {
        return requiredPoints;
    }

    public void setRequiredPoints(int requiredPoints) {
        this.requiredPoints = requiredPoints;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }
}
