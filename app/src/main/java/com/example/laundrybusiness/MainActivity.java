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
    private TextView welcomeText, activeOrdersText, emptyOrdersText; // ADDED emptyOrdersText

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Ensure R.id.main exists in activity_main.xml
        View rootLayout = findViewById(R.id.main);
        if (rootLayout != null) {
            ViewCompat.setOnApplyWindowInsetsListener(rootLayout, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }


        // Dynamic Welcome and Active Orders Text (USES Intent)
        welcomeText = findViewById(R.id.welcomeText);
        activeOrdersText = findViewById(R.id.activeOrdersText);
        emptyOrdersText = findViewById(R.id.emptyOrdersText); // INITIALIZE new TextView

        // Get username from Intent (passed from Login)
        String username = getIntent().getStringExtra("username");

        // CRASH PREVENTION: Null check before calling setText()
        if (welcomeText != null) {
            if (username != null && !username.isEmpty()) {
                welcomeText.setText("Welcome back, " + username);
            } else {
                welcomeText.setText("Welcome back, User");
            }
        }


        // Sample active orders count (MODIFIED to 0)
        int activeOrdersCount = 0;

        // CRASH PREVENTION: Null check before calling setText()
        if (activeOrdersText != null) {
            activeOrdersText.setText("You have " + activeOrdersCount + " active orders");
        }


        // Initialize dashboard
        initQuickActions();
        initRecentOrders();
    }

    // NOTE: To get the list to refresh automatically after placing an order,
    // you should override the onResume method:
    /*
    @Override
    protected void onResume() {
        super.onResume();
        initRecentOrders(); // Re-runs the data fetch when returning from PlaceOrderActivity
    }
    */


    // Quick Actions Click Listeners (Unchanged)
    private void initQuickActions() {
        MaterialCardView cardPlaceOrder = findViewById(R.id.cardPlaceOrder);
        MaterialCardView cardMyOrders = findViewById(R.id.cardMyOrders);
        MaterialCardView cardInvoice = findViewById(R.id.cardInvoice);

        if (cardPlaceOrder != null) {
            cardPlaceOrder.setOnClickListener(v -> {
                Intent intent = new Intent(this, PlaceOrderActivity.class);
                startActivity(intent);
            });
        }

        if (cardMyOrders != null) {
            cardMyOrders.setOnClickListener(v -> {
                Toast.makeText(this, "My Orders clicked! (TODO: Implement MyOrdersActivity)", Toast.LENGTH_SHORT).show();
                // TODO: Start MyOrdersActivity
            });
        }

        if (cardInvoice != null) {
            cardInvoice.setOnClickListener(v -> {
                Intent intent = new Intent(this, InvoiceActivity.class);
                startActivity(intent);
            });
        }
    }

    // Recent Orders Setup (MODIFIED to remove sample data and handle visibility)
    private void initRecentOrders() {
        recyclerRecentOrders = findViewById(R.id.recyclerRecentOrders);

        if (recyclerRecentOrders != null) {
            recyclerRecentOrders.setLayoutManager(new LinearLayoutManager(this));

            // MODIFIED: Start with an empty list. Data must be fetched via API.
            orderList = new ArrayList<>();

            // TODO: In a real app, API.fetchRecentOrders() would go here.

            OrderAdapter adapter = new OrderAdapter(orderList);
            recyclerRecentOrders.setAdapter(adapter);

            // Logic to show/hide based on content (Empty state)
            if (orderList.isEmpty()) {
                recyclerRecentOrders.setVisibility(View.GONE);
                if (emptyOrdersText != null) {
                    emptyOrdersText.setVisibility(View.VISIBLE);
                }
            } else {
                recyclerRecentOrders.setVisibility(View.VISIBLE);
                if (emptyOrdersText != null) {
                    emptyOrdersText.setVisibility(View.GONE);
                }
            }
        } else {
            System.err.println("FATAL: RecyclerView (recyclerRecentOrders) not found.");
        }
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
        public void onBindViewHolder(OrderAdapter.OrderViewHolder holder, int position) {
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