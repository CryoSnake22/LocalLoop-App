package com.example.localloop.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localloop.R;
import com.example.localloop.data.model.User;
import com.example.localloop.utils.UserUtils;

public class AdminDashboard extends AppCompatActivity {
    private User user;
    private TextView textWelcome;
    private LinearLayout userLayout,eventLayout,categoryLayout;
    private RecyclerView rvUser,rvEvent,rvCategory;
    private ImageView arrowUser,arrowEvent,arrowCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // This is the whole recycle view shenanigan thing










        textWelcome = findViewById(R.id.textWelcome);
        userLayout = findViewById(R.id.headerUsers);
        eventLayout = findViewById(R.id.headerEvents);
        rvUser = findViewById(R.id.rvUsers);
        rvEvent = findViewById(R.id.rvEvents);
        rvCategory = findViewById(R.id.rvCategories);
        arrowUser = findViewById(R.id.arrowUsers);
        arrowEvent = findViewById(R.id.arrowEvents);
        arrowCategory = findViewById(R.id.arrowCategories);
        categoryLayout = findViewById(R.id.headerCategories);

        userLayout.setOnClickListener(v->{
            if (rvUser.getVisibility()== View.GONE){
               rvUser.setVisibility(View.VISIBLE);
               arrowUser.setRotation(0);
            } else{
                rvUser.setVisibility(View.GONE);
                arrowUser.setRotation(-90);
            }
        });
        eventLayout.setOnClickListener(v->{
            if (rvEvent.getVisibility()== View.GONE){
                rvEvent.setVisibility(View.VISIBLE);
                arrowEvent.setRotation(0);
            } else{
                rvEvent.setVisibility(View.GONE);
                arrowEvent.setRotation(-90);
            }
        });
        categoryLayout.setOnClickListener(v->{
            if (rvCategory.getVisibility()== View.GONE){
                rvCategory.setVisibility(View.VISIBLE);
                arrowCategory.setRotation(0);

            } else{
                rvCategory.setVisibility(View.GONE);
                arrowCategory.setRotation(-90);
            }
        });

        Intent intent = getIntent();
        String UID = intent.getStringExtra("UID");
//        UserUtils.UIDtoUserAsync(UID, userAsync -> {
//            if (userAsync != null){
//                this.user = userAsync;
//                // String message = "Welcome " + user.getFirstName() + ", you are logged in as: " + user.getRole().toString().toLowerCase();
//                String message = "Welcome Admin, you are logged in as: admin";
//                textWelcome.setText(message);
//            }
//        });
        // Since we're bypassing I'll just set it manually
        String message = "Welcome Admin, you are logged in as: admin";
        textWelcome.setText(message);
    }
}