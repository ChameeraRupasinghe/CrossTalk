package cse15.chameera.crosstalk;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ChatActivity extends AppCompatActivity {

    private String UID;

    private TextView tUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        UID = intent.getExtras().getString("uid");

        tUID = (TextView) findViewById(R.id.userIDText);
        tUID.setText(UID);


    }


}
