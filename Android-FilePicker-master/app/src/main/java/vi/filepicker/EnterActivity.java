package vi.filepicker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class EnterActivity extends AppCompatActivity {

    private EditText bookName;
    private ImageView bookPhoto;
    private Button folderAdd;
    private RelativeLayout viewGroup;
    private View layout;
    private PopupWindow popup;
    private Bitmap photo;
    private int bookCount = 1;
    private Bitmap[] bookArray;
    private String[] bookPath;
    private String dirPath;
    private String folderPath;

    //책 이미지 버튼
    private ImageButton one;
    private ImageButton two;
    private ImageButton three;
    private ImageButton four;
    private ImageButton five;
    private ImageButton six;
    private ImageButton seven;
    private ImageButton eight;
    private ImageButton nine;
    private ImageButton ten;
    private ImageButton eleven;
    private ImageButton twelve;
    private ImageButton thirteen;
    private ImageButton fourteen;
    private ImageButton fifteen;

    private RelativeLayout main;
    private Button change;
    private boolean color = true;
    private int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSION_ONPICKDOC = new String[] {"android.permission.WRITE_EXTERNAL_STORAGE"};

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_main);

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Explain to the user why we need to read the contacts
            }


            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

            // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
            // app-defined int constant that should be quite unique
        }

        //책의 이미지경로를 넣어주기 위해서
        bookArray = new Bitmap[16];
        //책의 폴더 경로를 넣어주기 위해서
        bookPath = new String[16];

        //이미지 경로와 폴더 경로 초기화
        for (int i = 0; i < 16; i++) {
            bookArray[i] = null;
            bookPath[i] = null;
        }

        folderAdd = (Button) findViewById(R.id.imageButton);

        //메인화면에서 +버튼을 누를 경우 실행됨
        folderAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showPopup(EnterActivity.this);
            }
        });


        //북이미지 버튼을 선언
        one = (ImageButton) findViewById(R.id.one);
        two = (ImageButton) findViewById(R.id.two);
        three = (ImageButton) findViewById(R.id.three);
        four = (ImageButton) findViewById(R.id.four);
        five = (ImageButton) findViewById(R.id.five);
        six = (ImageButton) findViewById(R.id.six);
        seven = (ImageButton) findViewById(R.id.seven);
        eight = (ImageButton) findViewById(R.id.eight);
        nine = (ImageButton) findViewById(R.id.nine);
        ten = (ImageButton) findViewById(R.id.ten);
        eleven = (ImageButton) findViewById(R.id.eleven);
        twelve = (ImageButton) findViewById(R.id.twelve);
        thirteen = (ImageButton) findViewById(R.id.thirteen);
        fourteen = (ImageButton) findViewById(R.id.fourteen);
        fifteen = (ImageButton) findViewById(R.id.fifteen);


        //경로 읽어오는지 테스팅
//        one.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(),"폴더의 경로는 : " + bookPath[1],Toast.LENGTH_LONG).show();
//            }
//        });
//
//        two.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(),"폴더의 경로는 : " + bookPath[2],Toast.LENGTH_LONG).show();
//            }
//        });
//
//        three.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(),"폴더의 경로는 : " + bookPath[3],Toast.LENGTH_LONG).show();
//            }
//        });
    }

    // '+'버튼 누를 시 팝업창이 띄어진다.
    public void showPopup(final Activity context) {

        //이미지크기
        int popupWidth = 700;
        int popupHeight = 1300;

        //팝업창의 위치
        int OFFSET_X = 30;
        int OFFSET_Y = 30;

        //팝업창을 띄우기 위한 컨텐츠들
        viewGroup = (RelativeLayout) context.findViewById(R.id.popup);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = inflater.inflate(R.layout.popup_activity, viewGroup);

        //책이름(EditText)
        bookName = (EditText) layout.findViewById(R.id.getTitle);


        //팝업창 띄우기
        popup = new PopupWindow(context);
        popup.setContentView(layout);
        popup.setWidth(popupWidth);
        popup.setHeight(popupHeight);
        popup.setFocusable(true);
        popup.showAtLocation(layout, Gravity.CENTER, OFFSET_X, OFFSET_Y);

        //사진 선택 버튼
        Button select = (Button) layout.findViewById(R.id.select);

        // 사진 선택 버튼 클릭 시 internal 갤러리 띄우기
        select.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, 0);
                popup.setFocusable(false);
            }
        });

        //확인 버튼을 누를 시 (수정해야함)
        Button ok = (Button) layout.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();

                //북 이미지를 저장 어레이에 넣어준다.

                if (bookCount <= 15) {
                    bookArray[bookCount] = photo;
                }

                if (bookCount > 15) {
                    Toast.makeText(getApplicationContext(), "책장이 가득 찼습니다.", Toast.LENGTH_SHORT).show();
                    bookCount--;
                }


                //북이 1일 경우 1에 해당되는 이미지를 넣어주고 만들어준다.
                if (bookCount == 1) {
                    one.setImageBitmap(bookArray[1]);
                    one.setVisibility(View.VISIBLE);
                    bookPath[1] = folderPath;
                }

                if (bookCount == 2) {

                    two.setImageBitmap(bookArray[2]);
                    two.setVisibility(View.VISIBLE);
                    bookPath[2] = folderPath;
                }
                if (bookCount == 3) {

                    three.setImageBitmap(bookArray[3]);
                    three.setVisibility(View.VISIBLE);
                    bookPath[3] = folderPath;
                }
                if (bookCount == 4) {

                    four.setImageBitmap(bookArray[4]);
                    four.setVisibility(View.VISIBLE);
                    bookPath[4] = folderPath;
                }
                if (bookCount == 5) {

                    five.setImageBitmap(bookArray[5]);
                    five.setVisibility(View.VISIBLE);
                    bookPath[5] = folderPath;
                }
                if (bookCount == 6) {

                    six.setImageBitmap(bookArray[6]);
                    six.setVisibility(View.VISIBLE);
                    bookPath[6] = folderPath;
                }
                if (bookCount == 7) {

                    seven.setImageBitmap(bookArray[7]);
                    seven.setVisibility(View.VISIBLE);
                    bookPath[7] = folderPath;
                }
                if (bookCount == 8) {

                    eight.setImageBitmap(bookArray[8]);
                    eight.setVisibility(View.VISIBLE);
                    bookPath[8] = folderPath;
                }
                if (bookCount == 9) {

                    nine.setImageBitmap(bookArray[9]);
                    nine.setVisibility(View.VISIBLE);
                    bookPath[9] = folderPath;
                }
                if (bookCount == 10) {

                    ten.setImageBitmap(bookArray[10]);
                    ten.setVisibility(View.VISIBLE);
                    bookPath[10] = folderPath;
                }

                if (bookCount == 11) {

                    eleven.setImageBitmap(bookArray[11]);
                    eleven.setVisibility(View.VISIBLE);
                    bookPath[11] = folderPath;
                }
                if (bookCount == 12) {

                    twelve.setImageBitmap(bookArray[12]);
                    twelve.setVisibility(View.VISIBLE);
                    bookPath[12] = folderPath;
                }
                if (bookCount == 13) {

                    thirteen.setImageBitmap(bookArray[13]);
                    thirteen.setVisibility(View.VISIBLE);
                    bookPath[13] = folderPath;
                }
                if (bookCount == 14) {

                    fourteen.setImageBitmap(bookArray[14]);
                    fourteen.setVisibility(View.VISIBLE);
                    bookPath[14] = folderPath;
                }

                if (bookCount == 15) {

                    fifteen.setImageBitmap(bookArray[15]);
                    fifteen.setVisibility(View.VISIBLE);
                    bookPath[15] = folderPath;
                }

                bookCount++;
            }
        });

        //취소 버튼을 누를시
        Button cancel = (Button) layout.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                popup.dismiss();
            }
        });
    }

    // 갤러리에서 이미지 가져오기
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode != RESULT_OK)
            return;

        Uri getUri = data.getData();

        switch (requestCode) {

            // 가져온 이미지를 원하는 크기로 자르기
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

                // 이미지를 저장하기 위한 파일 경로
                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PicKeep/" + bookName.getText().toString() + "/" +
                        bookName.getText().toString() + ".jpg";
                folderPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PicKeep/" + bookName.getText().toString()+ "/";

                if (extras != null) {

                    photo = extras.getParcelable("data");
                    bookPhoto = (ImageView) layout.findViewById(R.id.getPic);

                    bookPhoto.setImageBitmap(photo); // 이미지 뷰에 그림 삽입하기
                    storeCropImage(photo, filePath);
                    break;
                }

                //원래 경로의 똑같은 파일 이름이 있으시 기존의 파일을 삭제한다.
                File f = new File(getUri.getPath());
                if (f.exists()) {
                    f.delete();
                }
        }
        popup.setFocusable(false);
    }

    //이미지를 사용자에 맞게 설정에 맞게 자르고, 그것을 해당 폴더에 저장한다.
    private void storeCropImage(Bitmap bitmap, String filePath) {
        dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PicKeep/";
        Log.i("Main","dirPath = "+dirPath);
        File directory_pickeep = new File(dirPath);

        //폴더가 존재하지 않는다면 새로운 디렉토리 폴더를 생성한다.
        if (!directory_pickeep.exists()) {
            directory_pickeep.mkdir();
            Log.i("aaaa","directory maked ? = "+directory_pickeep.exists());
        }

        File new_folder = new File(dirPath+"/"+bookName.getText().toString());
        if(!new_folder.exists())
        {
            new_folder.mkdir();
        }

        File copyFile = new File(filePath); // 이거 위에 if문 체크랑 같이 연결하자

        //비트맵 형식으로 파일을 만든다.
        try {
            FileOutputStream out = new FileOutputStream(copyFile);
            photo.compress(Bitmap.CompressFormat.JPEG,100,out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        bookPhoto.setImageBitmap(bitmap);
    }

    public void showContents(View view)
    {
        int id = view.getId();
        Intent intent = new Intent(this,MainActivity.class);
        switch(id)
        {
            case R.id.one :
                if(bookArray[1] != null) { intent.putExtra("path",bookPath[1]); Log.i("zzzz","bookPath[0] = "+bookPath[1]);} Log.i("zzzz","first button clicked"); break;
            case R.id.two :
                if(bookArray[2] != null) { intent.putExtra("path",bookPath[2]);} break;
            case R.id.three :
                if(bookArray[3] != null) { intent.putExtra("path",bookPath[3]); } break;
            case R.id.four :
                if(bookArray[4] != null) { intent.putExtra("path",bookPath[4]);} break;
            case R.id.five :
                if(bookArray[5] != null) { intent.putExtra("path",bookPath[5]);} break;
            case R.id.six :
                if(bookArray[6] != null) { intent.putExtra("path",bookPath[6]);} break;
            case R.id.seven :
                if(bookArray[7] != null) { intent.putExtra("path",bookPath[7]);} break;
            case R.id.eight :
                if(bookArray[8] != null) { intent.putExtra("path",bookPath[8]);} break;
            case R.id.nine :
                if(bookArray[9] != null) { intent.putExtra("path",bookPath[9]);} break;
            case R.id.ten :
                if(bookArray[10] != null) { intent.putExtra("path",bookPath[10]);} break;
            case R.id.eleven :
                if(bookArray[11] != null) { intent.putExtra("path",bookPath[11]);} break;
            case R.id.twelve :
                if(bookArray[12] != null) { intent.putExtra("path",bookPath[12]);} break;
            case R.id.thirteen :
                if(bookArray[13] != null) { intent.putExtra("path",bookPath[13]);} break;
            case R.id.fourteen:
                if(bookArray[14] != null) { intent.putExtra("path",bookPath[14]);} break;
            case R.id.fifteen:
                if(bookArray[15] != null) { intent.putExtra("path",bookPath[15]);} break;
        }
        if(intent.getStringExtra("path") != null) // 정상적인 폴더 위치를 클릭했을 경우
        {
            Log.i("zzzz","intent sending");
            startActivity(intent);
        }
    }
}