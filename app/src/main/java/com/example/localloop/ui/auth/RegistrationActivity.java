package com.example.localloop.ui.auth;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.localloop.R;
import com.example.localloop.data.model.Role;
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

    private String email, username, password,firstName,lastName;
    private String role;
    private FirebaseAuth mAuth;


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
            email = emailField.getText().toString().trim();
            username = usernameField.getText().toString().trim();
            password = passwordField.getText().toString().trim();
            firstName = firstNameField.getText().toString().trim();
            lastName = lastNameField.getText().toString().trim();


            //basic check
            if(email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email and password required", Toast.LENGTH_SHORT).show();
                return;
            }
            // Checking if participant/organizer button was selected
            if(!(participantButton.isChecked()||organizerButton.isChecked())){
               Toast.makeText(this, "Please select sign up as Organizer or Participant", Toast.LENGTH_SHORT).show();
               return;
            }
            if (participantButton.isChecked()){
                role = "PARTICIPANT";
            }
            else if (organizerButton.isChecked()){
                role = "ORGANIZER";
            }


            //firebase auth https://firebase.google.com/docs/auth/android/password-auth#java_1
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's informati
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null){
                                    String uid = user.getUid();
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    Map<String,String> profile = new HashMap<>();
                                    profile.put("firstName",firstName);
                                    profile.put("lastName",lastName);
                                    profile.put("userName",username);
                                    profile.put("email",user.getEmail());
                                    profile.put("role",role);
                                    db.collection("user_db").document(uid).set(profile);
                                    updateUI(user);
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

    //IMPORTANT TODO! Differenciate user type then send them into their corespond layout
    private void updateUI(FirebaseUser user) {

        if (user != null) {
            String email = user.getEmail();

            //Basic check
            if (email == null) {
                Toast.makeText(this, "User email not available", Toast.LENGTH_SHORT).show();
                return;
            }

            //Hard coded
            if (role.equals("ORGANIZER")) {
                setContentView(R.layout.activity_organizer_home);
            } else if (role.equals("PARTICIPANT")) {
                setContentView(R.layout.activity_participant_home);
            } else {
                Toast.makeText(this, "Unrecognized user role", Toast.LENGTH_SHORT).show();
            }
        }

        
    }
}
