package com.example.laundrybusiness;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets; // Import Insets explicitly

public class InvoiceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup for Edge-to-Edge display
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_invoice);

        // FIX for both errors:
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            // FIX 1: Requires R.id.main in the layout file.
            // FIX 2: Correctly applies padding and returns the insets object (WindowInsetsCompat).
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets; // Correct return type (WindowInsetsCompat)
        });

        // TODO: Implement logic here to fetch invoice data from your API
        // and populate a RecyclerView.
    }
}
