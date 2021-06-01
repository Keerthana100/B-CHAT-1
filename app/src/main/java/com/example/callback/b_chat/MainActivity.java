package com.example.callback.b_chat;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void chat(View view){
        Intent i = new Intent(this,ChatActivity.class);
        startActivity(i);
    }
    public void game(View view){
        Intent i = new Intent(this,MainActivity2.class);
        startActivity(i);
    }

}
