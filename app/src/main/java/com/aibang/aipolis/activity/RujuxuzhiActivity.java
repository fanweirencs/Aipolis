package com.aibang.aipolis.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.aibang.aipolis.R;


public class RujuxuzhiActivity extends AppCompatActivity {

    private Button button;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rujuxuzhi);

        TextView textView = (TextView)findViewById(R.id.id_top_title);
        textView.setText("入居须知");
        button = (Button) findViewById(R.id.id_needKnow);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RujuxuzhiActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


    public void back(View view){
        finish();
    }
}