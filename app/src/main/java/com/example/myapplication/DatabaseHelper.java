package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "food_ordering.db";
    private static final int DB_VERSION = 2;
    private static final String TABLE = "orders";

    public DatabaseHelper(Context context) { super(context, DB_NAME, null, DB_VERSION); }
    DatabaseHelper(Context context, String databaseName) { super(context, databaseName, null, DB_VERSION); }

    @Override public void onCreate(SQLiteDatabase db) { createOrders(db); }
    private void createOrders(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS orders (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "order_number TEXT NOT NULL COLLATE NOCASE UNIQUE, customer_name TEXT NOT NULL, " +
                "phone TEXT NOT NULL, food_item TEXT NOT NULL, " +
                "quantity INTEGER NOT NULL CHECK(quantity BETWEEN 1 AND 9999), " +
                "unit_price_cents INTEGER NOT NULL CHECK(unit_price_cents BETWEEN 1 AND 99999999), " +
                "status TEXT NOT NULL CHECK(status IN ('Pending','Preparing','Ready','Completed','Cancelled')))");
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) createOrders(db);
    }

    private ContentValues valuesFor(Order order) {
        ContentValues values = new ContentValues();
        values.put("order_number", order.getOrderNumber());
        values.put("customer_name", order.getCustomerName());
        values.put("phone", order.getPhone());
        values.put("food_item", order.getFoodItem());
        values.put("quantity", order.getQuantity());
        values.put("unit_price_cents", order.getUnitPriceCents());
        values.put("status", order.getStatus());
        return values;
    }

    public long insertOrder(Order order) {
        return getWritableDatabase().insertOrThrow(TABLE, null, valuesFor(order));
    }

    public int updateOrder(Order order) {
        return getWritableDatabase().update(TABLE, valuesFor(order), "id = ?", new String[]{String.valueOf(order.getId())});
    }

    public int deleteOrder(long id) {
        return getWritableDatabase().delete(TABLE, "id = ?", new String[]{String.valueOf(id)});
    }

    private Order fromCursor(Cursor c) {
        return new Order(c.getLong(c.getColumnIndexOrThrow("id")),
                c.getString(c.getColumnIndexOrThrow("order_number")),
                c.getString(c.getColumnIndexOrThrow("customer_name")),
                c.getString(c.getColumnIndexOrThrow("phone")),
                c.getString(c.getColumnIndexOrThrow("food_item")),
                c.getInt(c.getColumnIndexOrThrow("quantity")),
                c.getLong(c.getColumnIndexOrThrow("unit_price_cents")),
                c.getString(c.getColumnIndexOrThrow("status")));
    }

    public List<Order> getAllOrders() {
        List<Order> result = new ArrayList<>();
        try (Cursor c = getReadableDatabase().query(TABLE, null, null, null, null, null, "id DESC")) {
            while (c.moveToNext()) result.add(fromCursor(c));
        }
        return result;
    }

    public Order getOrder(long id) {
        try (Cursor c = getReadableDatabase().query(TABLE, null, "id = ?", new String[]{String.valueOf(id)}, null, null, null)) {
            return c.moveToFirst() ? fromCursor(c) : null;
        }
    }
}