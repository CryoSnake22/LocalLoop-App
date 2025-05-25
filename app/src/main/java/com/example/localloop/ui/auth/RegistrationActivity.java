package com.example.localloop.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.localloop.R;
import com.example.localloop.data.model.Role;
import com.example.localloop.data.model.User;
import com.example.localloop.utils.UserUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;


//Firebase guide https://firebase.google.com/docs/auth/android/password-auth#java_1
public class RegistrationActivity extends AppCompatActivity {
    private static final String TAG = "RegistrationActivity";

    private EditText emailField, usernameField, passwordField, firstNameField,lastNameField;
    private RadioButton organizerButton,participantButton;

    private FirebaseAuth mAuth;

    private String chosenRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_registration);

        emailField = findViewById(R.id.inputEmail);
        usernameField = findViewById(R.id.inputUsername);
        firstNameField = findViewById(R.id.inputFirstName);
        lastNameField = findViewById(R.id.inputLastName);
        passwordField = findViewById(R.id.inputPassword);
        organizerButton = findViewById(R.id.inputOrganizer);
        participantButton = findViewById(R.id.inputParticipant);
        Button submitButton = findViewById(R.id.buttonSubmit);

        submitButton.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();
            String username = usernameField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();
            String firstName = firstNameField.getText().toString().trim();
            String lastName = lastNameField.getText().toString().trim();



            //basic check
            if(email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email and password required", Toast.LENGTH_SHORT).show();
                return;
            }
            if (firstName.isEmpty()||lastName.isEmpty()){
                Toast.makeText(this, "Please input a first and last name", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!firstName.matches("[a-zA-Z\\s-]+") || !lastName.matches("[a-zA-z\\s-]+")){
                Toast.makeText(this, "Please only input alphabetical characters for the first and last name", Toast.LENGTH_SHORT).show();
                return;
            }
            // Checking if participant/organizer button was selected
            if(!(participantButton.isChecked()||organizerButton.isChecked())){
               Toast.makeText(this, "Please select sign up as Organizer or Participant", Toast.LENGTH_SHORT).show();
               return;
            }
            if (participantButton.isChecked()){
                chosenRole = "PARTICIPANT";
            }
            else if (organizerButton.isChecked()){
                chosenRole = "ORGANIZER";
            }


            //firebase auth https://firebase.google.com/docs/auth/android/password-auth#java_1
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                if (firebaseUser != null){
                                    String uid = firebaseUser.getUid();
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    Map<String,String> profile = new HashMap<>();
                                    profile.put("firstName",firstName);
                                    profile.put("lastName",lastName);
                                    profile.put("userName",username);
                                    profile.put("email",firebaseUser.getEmail());
                                    profile.put("role",chosenRole);
                                    db.collection("user_db").document(uid).set(profile);
                                    //LAMBDA!
                                    UserUtils.UIDtoUserAsync(uid, user ->{
                                        if (user != null){
                                            updateUI(user); // TO CHANGE TO OUR OWN USER CLASSES
                                        }
                                        else{
                                            Toast.makeText(RegistrationActivity.this, "User not found in DB",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(RegistrationActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }
                        }
                    });
        });
    }

    //firebase auth https://firebase.google.com/docs/auth/android/password-auth#java_1
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            reload();
        }
    }


    private void reload() {
    }


    private void updateUI(User user) {
        if (user != null) {
            String email = user.getEmail();
            Role role = user.getRole();
            String UID = user.getUID();

            //Basic check
            if (email == null) {
                Toast.makeText(this, "User email not available", Toast.LENGTH_SHORT).show();
                return;
            }
            if (UID == null){
                Toast.makeText(this, "UID not available", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent();
            // Make it so you can pass UID between views
            intent.putExtra("UID", UID);
            //Hard coded

            if (role == Role.ORGANIZER) {
                intent.setClass(this, OrganizerDashboard.class);
                startActivity(intent);
            } else if (role == Role.PARTICIPANT) {
                intent.setClass(this, ParticipantDashboard.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Unrecognized user role", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
