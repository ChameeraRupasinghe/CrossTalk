package cse15.chameera.crosstalk;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import Classes.User;

public class ChatActivity extends AppCompatActivity {

    private String UID, Name;


    private TextView tUID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        UID = intent.getExtras().getString("uid");

        tUID = (TextView) findViewById(R.id.userIDText);
        android.support.v7.widget.Toolbar mToolbar = (android.support.v7.widget.Toolbar)
                findViewById(R.id.chat_toolbar);
        setSupportActionBar(mToolbar);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);




        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference().child("user").child(UID).child("userName");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.getValue().toString();
                setUserName(name);
                tUID.setText(name);
                actionBar.setTitle(name);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("dataRefError", "Failed to get user name for uid"+ UID);

            }
        });
        //setUserName("Cha");


        //https://console.firebase.google.com/project/crosstalk-25904/database/crosstalk-25904/data/user/HKZkEUkZ7pZGBdq3aLCgT9jlDSg2/userName

        //tUID.setText("Hi " + UID + " " + userName);

        //setUserName((String) tUID.getText().toString());
        Log.i("userName", " user id is " + UID+ " user name is "+ Name);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                return true;

                default:
                    return super.onOptionsItemSelected(item);
        }

    }

    public void setUserName(String userName) {
        this.Name = userName;
    }
}
