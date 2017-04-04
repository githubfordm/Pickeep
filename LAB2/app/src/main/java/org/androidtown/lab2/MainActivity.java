package org.androidtown.lab2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    // Create variable Edit Text Name and Age, Button button1
    EditText Name;
    EditText Age;
    Button button1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Find each view through each id's name.
        Name = (EditText) findViewById(R.id.editText1);
        Age = (EditText) findViewById(R.id.editText2);
        button1 = (Button) findViewById(R.id.button1);

        //If you click button1,then it happen the event.
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Assign value that input string in name and age.
                String name = Name.getText().toString();
                String age = Age.getText().toString();

                // Change Main Activity into NewActivity in screen through intent.
                Intent intent = new Intent(getApplicationContext(), NewActivity.class);

                // Send data values that name and age .
                intent.putExtra("loginName", name);
                intent.putExtra("loginAge", age);
                startActivity(intent);
            }
        });

    }
}
