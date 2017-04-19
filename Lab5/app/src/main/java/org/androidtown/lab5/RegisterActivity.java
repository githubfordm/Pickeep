package org.androidtown.lab5;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by 박재성 on 2017-04-18.
 */

public class RegisterActivity extends AppCompatActivity {

    //Variables (Three textview, button)
    TextView nameText;
    TextView sexText;
    TextView issendText;
    Button button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        //get intent information
        Intent intent = getIntent();
        //get bundle in get intent
        Bundle myBundle = intent.getExtras();

        //Create each object by found id
        button2 = (Button) findViewById(R.id.button2);
        nameText = (TextView) findViewById(R.id.nameText);
        sexText = (TextView) findViewById(R.id.sexText);
        issendText = (TextView) findViewById(R.id.issendText);

        // Assign string values in correct variables of bundle
        String outName = myBundle.getString("edit");
        String outSex = myBundle.getString("radio");
        String outSand = myBundle.getString("check");

        // set text of three textview
        nameText.setText(outName);
        sexText.setText(outSex);
        issendText.setText(outSand);

        //If click this button then back.
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //finish current activity(RegisterActivity)
                finish();
            }
        });
    }
}
