package com.example.laundrybusiness;

import android.content.Intent; // ADDED: Import for Intent
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<OrderItem> items;
    // REMOVED: private View.OnClickListener onItemClickListener; (Unused field)

    public OrderAdapter(List<OrderItem> items) {
        this.items = items;
    }

    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // This is where item_recent_order.xml is used
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recent_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OrderViewHolder holder, int position) {
        OrderItem item = items.get(position);
        holder.orderIdText.setText(item.id);
        holder.orderDateStatusText.setText(item.dateStatus);
        holder.orderPriceText.setText("Total: " + item.price);

        // Notification/Status Icon Tint (Logic remains the same)
        if (item.isDelivered) {
            holder.orderStatusIcon.setImageResource(android.R.drawable.checkbox_on_background);
            int color = ContextCompat.getColor(holder.itemView.getContext(), R.color.primary);
            holder.orderStatusIcon.setImageTintList(ColorStateList.valueOf(color));
        } else {
            holder.orderStatusIcon.setImageResource(android.R.drawable.ic_lock_idle_alarm);
            int color = ContextCompat.getColor(holder.itemView.getContext(), R.color.gray);
            holder.orderStatusIcon.setImageTintList(ColorStateList.valueOf(color));
        }

        // Click listener for order item (whole card) - MODIFIED
        holder.itemView.setOnClickListener(v -> {
            // 1. Create the Intent for the detail activity
            Intent intent = new Intent(holder.itemView.getContext(), OrderDetailActivity.class);

            // 2. Pass the unique Order ID to the new activity
            intent.putExtra("ORDER_ID", item.id);

            // 3. Start the new activity
            holder.itemView.getContext().startActivity(intent);

            // Optional: Display a Toast for confirmation during testing
            Toast.makeText(holder.itemView.getContext(), "Opening details for " + item.id, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        ImageView orderStatusIcon, orderArrow;
        TextView orderIdText, orderDateStatusText, orderPriceText;

        public OrderViewHolder(View itemView) {
            super(itemView);
            orderStatusIcon = itemView.findViewById(R.id.orderStatusIcon);
            orderArrow = itemView.findViewById(R.id.orderArrow);
            orderIdText = itemView.findViewById(R.id.orderIdText);
            orderDateStatusText = itemView.findViewById(R.id.orderDateStatusText);
            orderPriceText = itemView.findViewById(R.id.orderPriceText);
        }
    }
}