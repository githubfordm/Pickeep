    package org.androidtown.lab5;

    import android.content.Intent;
    import android.support.v7.app.AppCompatActivity;
    import android.os.Bundle;
    import android.view.View;
    import android.widget.Button;
    import android.widget.CheckBox;
    import android.widget.EditText;
    import android.widget.RadioButton;
    import android.widget.RadioGroup;

    public class MainActivity extends AppCompatActivity {

        //Variables (button, editText, radio, checkbox)
        Button button1;

        EditText editText1;

        RadioGroup radioGroup;
        RadioButton man;
        RadioButton woman;

        CheckBox checkBox1;
        CheckBox checkBox2;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            //Each object find view by id
            editText1 = (EditText) findViewById(R.id.editText1);
            radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
            man = (RadioButton) findViewById(R.id.man);
            woman = (RadioButton) findViewById(R.id.woman);
            checkBox1 = (CheckBox) findViewById(R.id.checkbox1);
            checkBox2 = (CheckBox) findViewById(R.id.checkbox2);

            button1 = (Button) findViewById(R.id.button1);

            //Create button listener (if you click button1, then running this event)
            button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //create intent and bundle
                    Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                    Bundle myBundle = new Bundle();

                    //Assign string values
                    String txtEdit = editText1.getText().toString();
                    String txtcheckBox = "";
                    String txtradio = "";


                    //cehckBox condition
                    //If checked checkbox1 or checkbox2, then add correct message into txtchecBox
                    if (checkBox1.isChecked())
                        txtcheckBox += " SMS";
                    if (checkBox2.isChecked())
                        txtcheckBox += " e-mail";

                    //Get ID of checked radio button in radiogroup
                    int radioId = radioGroup.getCheckedRadioButtonId();

                    //If checked man radiobutton or woman radiobutton then add correct message into txtradio.
                    if (man.getId() == radioId)
                        txtradio += "man";
                    if (woman.getId() == radioId)
                        txtradio += "woman";

                    //Put all values into bundle
                    myBundle.putString("edit", txtEdit);
                    myBundle.putString("check", txtcheckBox);
                    myBundle.putString("radio", txtradio);

                    //Put bundle into intent
                    intent.putExtras(myBundle);

                    //send intent to register activity and start register activity
                    startActivity(intent);

                    //initial values
                    editText1.setText("");
                    checkBox1.setChecked(false);
                    checkBox2.setChecked(false);
                    man.setChecked(false);
                    woman.setChecked(false);
                }
            });

        }
    }
