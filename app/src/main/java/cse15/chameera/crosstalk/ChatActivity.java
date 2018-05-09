package cse15.chameera.crosstalk;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import Classes.ChatMessage;
import Classes.UserManager;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private String reciverID, reciverName, currentUserID;

    private Button mSendButton;
    private EditText mEditText;

    private DatabaseReference mDatabase, mChatDataBaseRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_new);

        Intent intent = getIntent();
        reciverID = intent.getExtras().getString("uid");
        currentUserID = intent.getExtras().getString("current_UID");


        //tUID = (TextView) findViewById(R.id.userIDText);
        android.support.v7.widget.Toolbar mToolbar = (android.support.v7.widget.Toolbar)
                findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference refUsernameRetreval = database.getReference().child("user").child(reciverID).child("userName");

        refUsernameRetreval.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.getValue().toString();
                setUserName(name);
                //tUID.setText(name);
                actionBar.setTitle(name);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("dataRefError", "Failed to get user name for uid" + reciverID);

            }
        });
        //setUserName("Cha");


        //https://console.firebase.google.com/project/crosstalk-25904/database/crosstalk-25904/data/user/HKZkEUkZ7pZGBdq3aLCgT9jlDSg2/userName

        //tUID.setText("Hi " + UID + " " + userName);

        //setUserName((String) tUID.getText().toString());
        Log.i("userName", " user id is " + reciverID + " user name is " + reciverName);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        setupDatabaseInstance();

        mSendButton = (Button) findViewById(R.id.btn_send);
        mEditText = (EditText) findViewById(R.id.edit_text_message);

        mSendButton.setOnClickListener(this);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void setUserName(String userName) {
        this.reciverName = userName;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public void onClick(View view) {
        String typedMessage = mEditText.getText().toString().trim();

        if (!typedMessage.isEmpty()) {
            ChatMessage newMessage = new ChatMessage(currentUserID, reciverID, typedMessage);

            mChatDataBaseRef.push().setValue(newMessage);

            mEditText.setText(" ");


        }
    }


    private void setupDatabaseInstance() {

        String[] userCouple = UserManager.arrangeAlphabeticalOrder(currentUserID, reciverID);

        mChatDataBaseRef = mDatabase.child("ChatMessages").child(userCouple[0]).child(userCouple[1]);


    }


}
