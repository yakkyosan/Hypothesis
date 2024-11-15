package com.example.qr_scanning.dao;

import androidx.sqlite.db.SupportSQLiteOpenHelper;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.example.qr_scanning.model.Item;

import java.util.ArrayList;
import java.util.List;

public class ManualItemDao implements ItemDao {

    private final SupportSQLiteOpenHelper openHelper;

    public ManualItemDao(SupportSQLiteOpenHelper openHelper) {
        this.openHelper = openHelper;
    }

    @Override
    public List<Item> getAllItems() {
        SupportSQLiteDatabase db = openHelper.getReadableDatabase();
        List<Item> items = new ArrayList<>();
        try (android.database.Cursor cursor = db.query("SELECT * FROM item_table")) {
            while (cursor.moveToNext()) {
                items.add(new Item(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("requiredPoints")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("imageResId"))
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }

    @Override
    public Item getItemById(int id) {
        SupportSQLiteDatabase db = openHelper.getReadableDatabase();
        Item item = null;
        try (android.database.Cursor cursor = db.query("SELECT * FROM item_table WHERE id = ?", new Object[]{id})) {
            if (cursor.moveToFirst()) {
                item = new Item(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("requiredPoints")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("imageResId"))
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return item;
    }

    @Override
    public void insert(Item item) {
        SupportSQLiteDatabase db = openHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            db.execSQL("INSERT INTO item_table (name, requiredPoints, imageResId) VALUES (?, ?, ?)",
                    new Object[]{item.getName(), item.getRequiredPoints(), item.getImageResId()});
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }
}
