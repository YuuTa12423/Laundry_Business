package com.example.laundrybusiness;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerRecentOrders;
    private Button viewAllButton;
    private List<OrderItem> orderList;
    private TextView welcomeText, activeOrdersText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Dynamic Welcome and Active Orders Text (USES Intent)
        welcomeText = findViewById(R.id.welcomeText);
        activeOrdersText = findViewById(R.id.activeOrdersText);

        // Get username from Intent (passed from Login)
        String username = getIntent().getStringExtra("username");
        if (username != null && !username.isEmpty()) {
            welcomeText.setText("Welcome back, " + username);
        } else {
            welcomeText.setText("Welcome back, User");  // Fallback
        }

        // Sample active orders count (hardcoded; replace with backend fetch)
        int activeOrdersCount = 2;  // TODO: Fetch from API
        activeOrdersText.setText("You have " + activeOrdersCount + " active orders");

        // Initialize dashboard
        initQuickActions();
        initRecentOrders();
    }

    // Quick Actions Click Listeners (MODIFIED)
    private void initQuickActions() {
        MaterialCardView cardPlaceOrder = findViewById(R.id.cardPlaceOrder);
        MaterialCardView cardMyOrders = findViewById(R.id.cardMyOrders);
        MaterialCardView cardInvoice = findViewById(R.id.cardInvoice); // Initialized Invoice Card

        cardPlaceOrder.setOnClickListener(v -> {
            // Start PlaceOrderActivity
            Intent intent = new Intent(this, PlaceOrderActivity.class);
            startActivity(intent);
        });

        cardMyOrders.setOnClickListener(v -> {
            Toast.makeText(this, "My Orders clicked! (TODO: Implement MyOrdersActivity)", Toast.LENGTH_SHORT).show();
            // TODO: Start MyOrdersActivity
        });

        // NEW: Invoice Click Listener
        cardInvoice.setOnClickListener(v -> {
            // Start InvoiceActivity
            Intent intent = new Intent(this, InvoiceActivity.class);
            startActivity(intent);
        });
    }

    // Recent Orders Setup (Unchanged)
    private void initRecentOrders() {
        recyclerRecentOrders = findViewById(R.id.recyclerRecentOrders);
        recyclerRecentOrders.setLayoutManager(new LinearLayoutManager(this));

        // Sample data (replace with backend fetch)
        orderList = new ArrayList<>();
        orderList.add(new OrderItem("Order #123", "Delivered on Oct 15, 2023", "$25.00", true));  // Delivered (primary blue)
        orderList.add(new OrderItem("Order #124", "Pending on Oct 16, 2023", "$30.00", false));  // Pending (gray)
        orderList.add(new OrderItem("Order #125", "Delivered on Oct 17, 2023", "$20.00", true));  // Delivered

        OrderAdapter adapter = new OrderAdapter(orderList);
        recyclerRecentOrders.setAdapter(adapter);
    }

    // Simple Data Model for Orders (Unchanged)
    public static class OrderItem {
        String id;
        String dateStatus;
        String price;
        boolean isDelivered;

        OrderItem(String id, String dateStatus, String price, boolean isDelivered) {
            this.id = id;
            this.dateStatus = dateStatus;
            this.price = price;
            this.isDelivered = isDelivered;
        }
    }

    // RecyclerView Adapter for Recent Orders (Unchanged)
    public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
        private List<OrderItem> items;

        OrderAdapter(List<OrderItem> items) {
            this.items = items;
        }

        @Override
        public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recent_order, parent, false);
            return new OrderViewHolder(view);
        }

        @Override
        public void onBindViewHolder(OrderViewHolder holder, int position) {
            OrderItem item = items.get(position);
            holder.orderIdText.setText(item.id);
            holder.orderDateStatusText.setText(item.dateStatus);
            holder.orderPriceText.setText("Total: " + item.price);

            // Notification/Status Icon Tint (USES ContextCompat and ColorStateList)
            if (item.isDelivered) {
                holder.orderStatusIcon.setImageResource(android.R.drawable.checkbox_on_background);  // Check icon
                int color = ContextCompat.getColor(MainActivity.this, R.color.primary);
                holder.orderStatusIcon.setImageTintList(ColorStateList.valueOf(color));
            } else {
                holder.orderStatusIcon.setImageResource(android.R.drawable.ic_lock_idle_alarm);  // Clock icon
                int color = ContextCompat.getColor(MainActivity.this, R.color.gray);
                holder.orderStatusIcon.setImageTintList(ColorStateList.valueOf(color));
            }

            // Click listener for order item (whole card)
            holder.itemView.setOnClickListener(v -> {
                Toast.makeText(MainActivity.this, "Viewing details for " + item.id + " (TODO: Implement OrderDetailActivity)", Toast.LENGTH_SHORT).show();
                // TODO: Start OrderDetailActivity with item.id
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class OrderViewHolder extends RecyclerView.ViewHolder {
            ImageView orderStatusIcon, orderArrow;
            TextView orderIdText, orderDateStatusText, orderPriceText;

            OrderViewHolder(View itemView) {
                super(itemView);
                orderStatusIcon = itemView.findViewById(R.id.orderStatusIcon);
                orderArrow = itemView.findViewById(R.id.orderArrow);
                orderIdText = itemView.findViewById(R.id.orderIdText);
                orderDateStatusText = itemView.findViewById(R.id.orderDateStatusText);
                orderPriceText = itemView.findViewById(R.id.orderPriceText);
            }
        }
    }
}