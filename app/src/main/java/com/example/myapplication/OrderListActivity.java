package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class OrderListActivity extends AppCompatActivity implements OrderAdapter.OrderActionListener {
    private DatabaseHelper db;
    private OrderAdapter adapter;
    private TextView empty, count;

    @SuppressLint("MissingInflatedId")
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);
        db = new DatabaseHelper(this);
        empty = findViewById(R.id.tvEmpty);
        count = findViewById(R.id.tvOrderCount);
        RecyclerView recyclerView = findViewById(R.id.recyclerOrders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderAdapter(this);
        recyclerView.setAdapter(adapter);
        findViewById(R.id.btnAddFromList).setOnClickListener(v ->
                startActivity(new Intent(this, AddOrderActivity.class)));
    }

    @Override protected void onResume() {
        super.onResume();
        List<Order> orders = db.getAllOrders();
        adapter.setOrders(orders);
        int size = orders.size();
        count.setText(size + (size == 1 ? " order" : " orders"));
        empty.setVisibility(size == 0 ? View.VISIBLE : View.GONE);
    }

    @Override public void onEdit(Order order) {
        Intent intent = new Intent(this, EditOrderActivity.class);
        intent.putExtra(EditOrderActivity.EXTRA_ID, order.getId());
        startActivity(intent);
    }

    @Override public void onDelete(Order order) {
        new AlertDialog.Builder(this)
                .setTitle("Delete order?")
                .setMessage("Delete " + order.getOrderNumber() + "? This cannot be undone.")
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Delete", (dialog, which) -> {
                    if (db.deleteOrder(order.getId()) > 0) {
                        Toast.makeText(this, "Order deleted", Toast.LENGTH_SHORT).show();
                        refreshList();
                    } else {
                        Toast.makeText(this, "Order not found", Toast.LENGTH_SHORT).show();
                    }
                }).show();
    }

    private void refreshList() {
        List<Order> orders = db.getAllOrders();
        adapter.setOrders(orders);
        int size = orders.size();
        count.setText(size + (size == 1 ? " order" : " orders"));
        empty.setVisibility(size == 0 ? View.VISIBLE : View.GONE);
    }

    @Override protected void onDestroy() { db.close(); super.onDestroy(); }
}