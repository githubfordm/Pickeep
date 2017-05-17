package org.androidtown.pcipic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;


public class MainActivity extends AppCompatActivity {

    public int index=0;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.imageButton);

        button.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v){

                showPopup(MainActivity.this);

            }
        });
    }

    public void showPopup(final Activity context){

        final boolean hasClicked=false;

        int popupWidth = 700;
        int popupHeight = 850;

        int OFFSET_X = 30;
        int OFFSET_Y = 30;

        RelativeLayout viewGroup = (RelativeLayout) context.findViewById(R.id.popup);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.popup_activity, viewGroup);

        final PopupWindow popup = new PopupWindow(context);
        popup.setContentView(layout);
        popup.setWidth(popupWidth);
        popup.setHeight(popupHeight);
        popup.setFocusable(true);

        popup.showAtLocation(layout, Gravity.CENTER, OFFSET_X, OFFSET_Y);

        Button select = (Button) layout.findViewById(R.id.select);
        final ImageView img = (ImageView) layout.findViewById(R.id.getBook);

        select.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v){

                selectBook(MainActivity.this);

            }
        });

        Button cancel = (Button) layout.findViewById(R.id.cancel);

        cancel.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v){

                popup.dismiss();
            }
        });

    }


    public void selectBook(final Activity context){

        int popupWidth = 1000;
        int popupHeight = 800;

        int OFFSET_X = 30;
        int OFFSET_Y = 30;

        RelativeLayout viewGroup = (RelativeLayout) context.findViewById(R.id.book_popup);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.book_popup_activity, viewGroup);

        ImageButton ib1 = (ImageButton) layout.findViewById(R.id.getBook1);
        ImageButton ib2 = (ImageButton) layout.findViewById(R.id.getBook2);
        ImageButton ib3 = (ImageButton) layout.findViewById(R.id.getBook3);

        final RadioButton rb1 = (RadioButton) layout.findViewById(R.id.radio1);
        final RadioButton rb2 = (RadioButton) layout.findViewById(R.id.radio2);
        final RadioButton rb3 = (RadioButton) layout.findViewById(R.id.radio3);

        final PopupWindow popup = new PopupWindow(context);
        popup.setContentView(layout);
        popup.setWidth(popupWidth);
        popup.setHeight(popupHeight);
        popup.setFocusable(true);

        popup.showAtLocation(layout, Gravity.CENTER, OFFSET_X, OFFSET_Y);

        Button ok = (Button) layout.findViewById(R.id.ok);
        Button cancel = (Button) layout.findViewById(R.id.cancel);

        ib1.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v){

                rb1.setChecked(true);
                rb2.setChecked(false);
                rb3.setChecked(false);

            }
        });

        ib2.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v){

                rb1.setChecked(false);
                rb2.setChecked(true);
                rb3.setChecked(false);

            }
        });


        ib3.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v){

                rb1.setChecked(false);
                rb2.setChecked(false);
                rb3.setChecked(true);

            }
        });

        ok.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v){

                //chekck the radio button
                if(rb1.isChecked())
                    index=1;

                else if(rb2.isChecked())
                    index=2;

                else if(rb3.isChecked())
                    index=3;

                popup.dismiss();

            }
        });

        cancel.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v){

                popup.dismiss();
            }
        });

    }

}
