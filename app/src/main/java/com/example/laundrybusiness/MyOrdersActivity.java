package com.example.laundrybusiness;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.activity.EdgeToEdge;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets; // Added import for Insets

import java.util.ArrayList;
import java.util.List;

public class MyOrdersActivity extends AppCompatActivity {

    private RecyclerView recyclerAllOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_orders);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // FIX: Line 20: Resolved by ensuring activity_my_orders.xml has R.id.recyclerAllOrders
        recyclerAllOrders = findViewById(R.id.recyclerAllOrders);

        if (recyclerAllOrders != null) {
            recyclerAllOrders.setLayoutManager(new LinearLayoutManager(this));

            // FIX for Line 30: Reference the external OrderItem and OrderAdapter directly
            List<OrderItem> allOrderList = loadAllOrders();

            OrderAdapter adapter = new OrderAdapter(allOrderList); // DIRECT REFERENCE
            recyclerAllOrders.setAdapter(adapter);
        }
    }

    // FIX for lines 38-40: loadAllOrders now uses the external OrderItem class
    private List<OrderItem> loadAllOrders() {
        List<OrderItem> list = new ArrayList<>();
        list.add(new OrderItem("Order #999", "Completed on Nov 1, 2023", "$15.00", true));
        list.add(new OrderItem("Order #888", "Pending on Oct 28, 2023", "$45.00", false));
        list.add(new OrderItem("Order #777", "Delivered on Oct 20, 2023", "$22.50", true));
        return list;
    }
}