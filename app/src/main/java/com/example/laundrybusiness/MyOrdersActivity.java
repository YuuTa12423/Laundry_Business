package com.example.laundrybusiness;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.activity.EdgeToEdge;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;

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

        recyclerAllOrders = findViewById(R.id.recyclerAllOrders);

        if (recyclerAllOrders != null) {
            recyclerAllOrders.setLayoutManager(new LinearLayoutManager(this));

            List<OrderItem> allOrderList = loadAllOrders();

            OrderAdapter adapter = new OrderAdapter(allOrderList);
            recyclerAllOrders.setAdapter(adapter);
        }
    }

    // FIX: loadAllOrders now passes status strings instead of boolean
    private List<OrderItem> loadAllOrders() {
        List<OrderItem> list = new ArrayList<>();
        // Args: ID, DateStatus, Price, Status_String
        list.add(new OrderItem("Order #999", "Completed on Nov 1, 2023", "$15.00", "DELIVERED"));
        list.add(new OrderItem("Order #888", "Pending on Oct 28, 2023", "$45.00", "PENDING"));
        list.add(new OrderItem("Order #777", "Delivered on Oct 20, 2023", "$22.50", "DELIVERED"));
        list.add(new OrderItem("Order #666", "Processing on Oct 19, 2023", "$12.00", "IN_PROCESS"));
        return list;
    }
}