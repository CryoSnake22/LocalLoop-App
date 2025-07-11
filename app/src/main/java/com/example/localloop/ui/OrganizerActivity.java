package com.example.localloop.ui;

import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.localloop.R;
import com.example.localloop.databse.Category;
import com.example.localloop.databse.CategoryOperation;
import com.example.localloop.databse.Event;
import com.example.localloop.databse.EventOperation;
import com.example.localloop.databse.UserOperation;
import com.example.localloop.usertype.OrganizerUser;


/*
Organizers Can:

View all events he owns and open it
-> View all request to join in that event
-> Accept request to join that event
-> View all accepted request to join that event



 */
public class OrganizerActivity extends AppCompatActivity {
    private void adminHomeLayout() {
        Log.d("LAYOUT", "THIS IS organizer home activity PAGE");
        setContentView(R.layout.organizer_home_activity);

        //MANAGE EVENTS CLICKED
        Button btn_organizer_manage_events = findViewById(R.id.btn_organizer_manage_events);
        btn_organizer_manage_events.setOnClickListener(v -> manageEvents());

        //MANAGE USERS CLICKED
        Button btn_organizer_pending_request = findViewById(R.id.btn_organizer_pending_request);
        btn_organizer_pending_request.setOnClickListener(v -> manageRequest());
    }

    private void manageEvents() {
        Log.d("LAYOUT", "THIS IS organizer_manage_event_activity PAGE");
        setContentView(R.layout.organizer_manage_event_activity);

        Button btn_organizer_create_event = findViewById(R.id.btn_organizer_create_event);
        btn_organizer_create_event.setOnClickListener(v -> createEvent());


    }



    private void manageRequest() {
        Log.d("LAYOUT", "THIS IS organizer_manage_request_activity PAGE");
        setContentView(R.layout.organizer_manage_request_activity);




    }


    private void createEvent() {
        Log.d("LAYOUT", "THIS IS organizer_add_event_activity PAGE");
        setContentView(R.layout.organizer_add_event_activity);


        Button btnSubmit = findViewById(R.id.btn_organizer_add_event_submit);
        EditText eventNameField = findViewById(R.id.text_organizer_add_event_name);
        EditText eventDescriptionField = findViewById(R.id.text_organizer_add_event_description);
        EditText eventFeeField = findViewById(R.id.text_organizer_add_event_fee);
        EditText eventDateField = findViewById(R.id.text_organizer_add_event_date);
        EditText eventTimeField = findViewById(R.id.text_organizer_add_event_time);


        btnSubmit.setOnClickListener(v -> {
            String name = eventNameField.getText().toString().trim();
            String description = eventDescriptionField.getText().toString().trim();
            String fee = eventFeeField.getText().toString().trim();
            String date = eventDateField.getText().toString().trim();
            String time = eventTimeField.getText().toString().trim();

            Event event = new Event(name, description, "", Float.parseFloat(fee), date, time, (OrganizerUser) UserOperation.currentUser);
            EventOperation.addEvent(UserOperation.currentUser.getEmail(), event);
            Toast.makeText(this, "Event Created", Toast.LENGTH_SHORT).show();

            manageEvents();
        });

    }
}
