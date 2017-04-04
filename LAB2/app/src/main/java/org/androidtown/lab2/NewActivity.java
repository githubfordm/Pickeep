package org.androidtown.lab2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class NewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

        //Get Passed intent value.
        Intent passedIntent = getIntent();

        //If passedIntent have intent value, assign to string variables intent's string name value and string age value.
        if (passedIntent != null) {
            String loginName = passedIntent.getStringExtra("loginName");
            String loginAge = passedIntent.getStringExtra("loginAge");
            //Show student info value in screen.
            Toast.makeText(getApplication(), "Student info : " + loginName + ", " + loginAge, Toast.LENGTH_LONG).show();
        }

        //If click button2, finish NewActivity.
        Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
