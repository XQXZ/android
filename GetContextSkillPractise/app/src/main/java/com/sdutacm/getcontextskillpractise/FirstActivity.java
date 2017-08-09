package com.sdutacm.getcontextskillpractise;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class FirstActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Person person = new Person();
        person.setAge(18);
        person.setName("Tom");
        Intent intent = new Intent(FirstActivity.this,SecondActivity.class);
        intent.putExtra("person_data",person);
        startActivity(intent);
    }
}
