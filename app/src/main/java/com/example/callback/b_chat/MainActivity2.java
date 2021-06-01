package com.example.callback.b_chat;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;


public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
    }

    public void jumble(View view){
        Intent i = new Intent(this,jumble.class);
        startActivity(i);
    }
    public void vowel(View view){
        Intent i = new Intent(this,vowel.class);
        startActivity(i);
    }

}
