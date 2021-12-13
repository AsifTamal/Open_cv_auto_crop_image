package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class sendActivity extends AppCompatActivity {

    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        button=findViewById(R.id.btnSend);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ////sent to broadcast//////////
                final Intent i= new Intent();
                i.putExtra("vr_data", "True");
                i.setAction("com.example.batcampaign");
                i.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                getApplicationContext().sendBroadcast(i);
                ///for finish activity//////////
                finishAffinity();
            }
        });
    }
}