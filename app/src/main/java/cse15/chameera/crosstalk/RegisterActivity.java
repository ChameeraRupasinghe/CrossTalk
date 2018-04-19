package cse15.chameera.crosstalk;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import Classes.User;

public class RegisterActivity extends AppCompatActivity {

    private EditText mEmail, mPassword, mName, mRePassword;
    private Button mReg;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user != null) {
                    Intent intent = new Intent(RegisterActivity.this, RegisterActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }

            }
        };


        mEmail = (EditText) findViewById(R.id.rEmail);
        mPassword = (EditText) findViewById(R.id.rPassword);
        mName = (EditText) findViewById(R.id.rName);
        mRePassword = (EditText) findViewById(R.id.rRepeatPassword);

        mReg = (Button) findViewById(R.id.reg);

        mReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!TextUtils.isEmpty(mEmail.getText().toString()) &&
                        !TextUtils.isEmpty(mPassword.getText().toString()) &&
                        !TextUtils.isEmpty(mName.getText().toString()) &&
                        !TextUtils.isEmpty(mRePassword.getText().toString())) {

                    if (Objects.equals(mPassword.getText().toString(), mRePassword.getText().toString())) {


                        final String email = mEmail.getText().toString();
                        final String password = mPassword.getText().toString();
                        final String name = mName.getText().toString();

                        final User user = new User(name, email);


                        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    FirebaseAuthException e = (FirebaseAuthException) task.getException();
                                    Toast.makeText(RegisterActivity.this, "Sign Up Error!", Toast.LENGTH_SHORT).show();
                                    Log.e("Registration Activity", "Failed Registration", e);
                                } else {
                                    String user_id = mAuth.getCurrentUser().getUid();
                                    DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("user").child(user_id);
                                    current_user_db.setValue(user);

                                    FirebaseUser currentUser = mAuth.getCurrentUser();
                                    UserProfileChangeRequest profileUpdates = new
                                            UserProfileChangeRequest.Builder()
                                            .setDisplayName(name)
                                            .build();

                                    currentUser.updateProfile(profileUpdates)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        Log.i("Username save", "Username Saved");
                                                    }
                                                }
                                            });



                                }
                            }
                        });

                    } else {
                        mRePassword.getText().clear();
                        Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    }


                } else {
                    Toast.makeText(RegisterActivity.this, "Fill out all feilds", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthListener);
    }
}
