package com.example.myapplication;

import android.os.Bundle;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteException;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddOrderActivity extends AppCompatActivity {
    public static final String EXTRA_ID = "order_database_id";
    private EditText number, customer, phone, food, quantity, price;
    private Spinner status;
    private DatabaseHelper db;
    private long recordId = -1;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_order);
        db = new DatabaseHelper(this);
        number = findViewById(R.id.etOrderNumber);
        customer = findViewById(R.id.etCustomerName);
        phone = findViewById(R.id.etPhone);
        food = findViewById(R.id.etFoodItem);
        quantity = findViewById(R.id.etQuantity);
        price = findViewById(R.id.etUnitPrice);
        status = findViewById(R.id.spStatus);
        ArrayAdapter<String> statuses = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Order.STATUSES);
        statuses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        status.setAdapter(statuses);

        findViewById(R.id.btnSave).setOnClickListener(v -> saveOrder());
    }

    private void saveOrder() {
        Order order = OrderFormValidator.validate(recordId, number, customer, phone, food, quantity, price, status);
        if (order == null) return;
        try {
            if (db.insertOrder(order) > 0) {
                Toast.makeText(this, "Order saved. Total: " + Order.formatMoney(order.getTotalCents()), Toast.LENGTH_LONG).show();
                finish();
            } else Toast.makeText(this, "Order could not be saved", Toast.LENGTH_LONG).show();
        } catch (SQLiteConstraintException e) {
            number.setError("Order number already exists"); number.requestFocus();
        } catch (SQLiteException e) {
            Toast.makeText(this, "Unable to save order. Please try again.", Toast.LENGTH_LONG).show();
        }
    }

    @Override protected void onDestroy() { if (db != null) db.close(); super.onDestroy(); }
}