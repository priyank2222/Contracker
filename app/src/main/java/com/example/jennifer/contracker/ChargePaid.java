package com.example.jennifer.contracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Jennifer on 4/15/2018.
 */

public class ChargePaid extends AppCompatActivity{

    Button btnreturn;
    TextView sentinfo;
    String statement;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paymentsubmitted);
        sentinfo = (TextView) findViewById(R.id.txtSubmitted);
        sentinfo.setText(getIntent().getExtras().getString("submittedinfo"));
        btnreturn = (Button) findViewById(R.id.btnreturn);

        //return to main menu button listener
        btnreturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnhome = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(returnhome);
                finish();
            }
        });
    }

}
