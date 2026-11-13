package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    public interface OrderActionListener {
        void onEdit(Order order);
        void onDelete(Order order);
    }
    private final List<Order> orders = new ArrayList<>();
    private final OrderActionListener listener;

    public OrderAdapter(OrderActionListener listener) { this.listener = listener; }

    public void setOrders(List<Order> newOrders) {
        orders.clear();
        orders.addAll(newOrders);
        notifyDataSetChanged();
    }

    @NonNull @Override public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.name.setText("#" + order.getOrderNumber() + " - " + order.getCustomerName());
        holder.details.setText("Phone: " + order.getPhone() +
                "\nFood: " + order.getFoodItem() +
                "\nQuantity: " + order.getQuantity() + " | Unit price: " + Order.formatMoney(order.getUnitPriceCents()) +
                "\nTotal: " + Order.formatMoney(order.getTotalCents()) + " | Status: " + order.getStatus());
        holder.edit.setOnClickListener(v -> listener.onEdit(order));
        holder.delete.setOnClickListener(v -> listener.onDelete(order));
    }

    @Override public int getItemCount() { return orders.size(); }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        final TextView name, details;
        final Button edit, delete;
        OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvName);
            details = itemView.findViewById(R.id.tvDetails);
            edit = itemView.findViewById(R.id.btnEdit);
            delete = itemView.findViewById(R.id.btnDelete);
        }
    }
}