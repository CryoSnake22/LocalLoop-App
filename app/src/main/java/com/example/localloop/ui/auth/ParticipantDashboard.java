package com.example.localloop.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.localloop.R;
import com.example.localloop.data.model.User;
import com.example.localloop.utils.UserUtils;

public class ParticipantDashboard extends AppCompatActivity {

    private User user;
    private TextView textWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_participant_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textWelcome = findViewById(R.id.textWelcome);
        Intent intent = getIntent();
        String UID = intent.getStringExtra("UID");
        UserUtils.UIDtoUserAsync(UID, userAsync -> {
            if (userAsync != null) {
                this.user = userAsync;
                String message = "Welcome " + user.getFirstName() + ", you are logged in as: " + user.getRole().toString().toLowerCase();
                textWelcome.setText(message);
            }
        });
    }
}
