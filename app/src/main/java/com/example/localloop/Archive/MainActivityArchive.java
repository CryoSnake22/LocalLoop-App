package com.example.localloop;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.yourapp.utils.DbUtil;

import java.util.HashMap;
import java.util.Map;

public class MainActivityArchive extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example Firestore call
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Alice");
        data.put("email", "alice@example.com");

        DbUtil.add("users", "user123", data);

        DbUtil.get("users", "user123", documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Log.d("MainActivity", "User name: " + documentSnapshot.getString("name"));
            } else {
                Log.d("MainActivity", "Document does not exist");
            }
        }, e -> Log.e("MainActivity", "Error fetching doc", e));

        DbUtil.delete("users", "user123");
    }
}
