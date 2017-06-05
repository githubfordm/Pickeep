package org.androidtown.pcipic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import static java.security.AccessController.getContext;


public class MainActivity extends AppCompatActivity {

    public int index=0;
    private ImageView bookPhoto;
    private Uri imageCaptureUri;
    private String absolutePath;
    private Bitmap photo=null;
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

    // '+'버튼 누를 시 팝업창 생성
    public void showPopup(final Activity context){

        //final boolean hasClicked;

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
        bookPhoto = (ImageView) this.findViewById(R.id.getPic);

        if(photo != null)
            bookPhoto.setImageBitmap(photo);

        // 버튼 클릭 시 사진 앨범창 띄우기
        select.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v){

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, 0);
                popup.dismiss();

            }
        });

        switch (index) {
            case 1:
                Drawable myImg = getResources().getDrawable(R.drawable.book_name1);
                //img.setImageDrawable(myImg);
                break;
            case 2:
                Drawable myImg2 = getResources().getDrawable(R.drawable.book_name2);
                //img.setImageDrawable(myImg2);
                break;
            case 3:
                Drawable myImg3 = getResources().getDrawable(R.drawable.book_name3);
                //img.setImageDrawable(myImg3);
                break;
        }

        Button cancel = (Button) layout.findViewById(R.id.cancel);

        cancel.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v){

                popup.dismiss();
            }
        });

    }

    // 앨범 창 띄우기
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_OK)
            return;

        Uri getUri = data.getData();

        switch(requestCode) {

            // 리사이즈할 이미지 결정
            case 0:
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(getUri, "image/*");

                // 200*200 크기로 저장
                intent.putExtra("outputX", 200);
                intent.putExtra("outputY", 200);
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, 1);
                break;

            // 리사이즈한 이미지 넘겨 받음
            case 1:
                if (resultCode != RESULT_OK)
                    return;

                final Bundle extras = data.getExtras();

                long getLong = System.currentTimeMillis();
                String filename = Long.toString(getLong);
                // 이미지를 저장하기 위한 파일 경로
                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PicKeep/" + filename + ".jpg";

                if (extras != null) {

                    photo = extras.getParcelable("data"); // 리사이즈된 BItmap
                    //bookPhoto.setImageBitmap(photo); // 레이아웃의 이미지칸에 CROP된 bitmap을 보여줌

                    storeCropImage(photo, filePath);
                    absolutePath = filePath;
                    break;

                }

                File f = new File(getUri.getPath());
                if (f.exists()) {
                    f.delete();
                }
        }

        showPopup(MainActivity.this);

    }

    // 이미지 저장
    private void storeCropImage(Bitmap bitmap, String filePath){
        //String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/PicKeep";
        File directory_pickeep = new File(filePath);
        if(!directory_pickeep.exists())
            directory_pickeep.mkdir();

        File copyFile = new File(filePath);
        BufferedOutputStream out = null;

        try{
            copyFile.createNewFile();
            out = new BufferedOutputStream(new FileOutputStream(copyFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.fromFile(copyFile)));

            out.flush();
            out.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        photo = BitmapFactory.decodeFile(copyFile.getAbsolutePath());

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

                showPopup(MainActivity.this);
                popup.dismiss();

            }
        });

        cancel.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v){

                showPopup(MainActivity.this);
                popup.dismiss();
            }
        });

    }

}
