package vi.filepicker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import permissions.dispatcher.PermissionUtils;

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
    private ImageButton button_list[] = new ImageButton[15];
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

    private int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSION_ONPICKDOC = new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"};

    ImageView deskImage;

    private int index;
    private PopupWindow delete_window;
    File directory_pickeep;

    private View.OnLongClickListener listener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            popup_delete(v);
            return true;
        }
    };

    private View deleteObject;
    private int BACKGROUND_MODE = 4;


    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_main);

        Intent intent = new Intent(this, LoadingActivity.class);
        startActivity(intent);

        //책장배경
        deskImage = (ImageView) findViewById(R.id.deskImage);

        if(!PermissionUtils.hasSelfPermissions(this,PERMISSION_ONPICKDOC)) // 권한 체크 안되있으면
        {
            ActivityCompat.requestPermissions(this, PERMISSION_ONPICKDOC, 1);   // 권한 요청 창 띄우기
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }
        else // 권한 체크 이상없으면
            init();
    }

    private void init()
    {
            dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PicKeep/";
            Log.i("Main", "dirPath = " + dirPath);
            directory_pickeep = new File(dirPath);

            //폴더가 존재하지 않는다면 새로운 디렉토리 폴더를 생성한다.
            if (!directory_pickeep.exists()) {
                directory_pickeep.mkdir();
                Log.i("aaaa", "directory maked ? = " + directory_pickeep.exists());
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
            one = (ImageButton) findViewById(R.id.one); button_list[1] = one;
            two = (ImageButton) findViewById(R.id.two); button_list[2] = two;
            three = (ImageButton) findViewById(R.id.three); button_list[3] = three;
            four = (ImageButton) findViewById(R.id.four); button_list[4] = four;
            five = (ImageButton) findViewById(R.id.five); button_list[5] = five;
            six = (ImageButton) findViewById(R.id.six); button_list[6] = six;
            seven = (ImageButton) findViewById(R.id.seven); button_list[7] = seven;
            eight = (ImageButton) findViewById(R.id.eight); button_list[8] = eight;
            nine = (ImageButton) findViewById(R.id.nine); button_list[9] = nine;
            ten = (ImageButton) findViewById(R.id.ten); button_list[10] = ten;
            eleven = (ImageButton) findViewById(R.id.eleven); button_list[11] = eleven;
            twelve = (ImageButton) findViewById(R.id.twelve); button_list[12] = twelve;
            thirteen = (ImageButton) findViewById(R.id.thirteen); button_list[13] = thirteen;
            fourteen = (ImageButton) findViewById(R.id.fourteen); button_list[14] = fourteen;

            setBackground();
            setList();
            registerLongClick();
    }

    private void setBackground()
    {
        try {
            InputStreamReader in = new InputStreamReader(openFileInput("setting.txt"));
            BufferedReader reader = new BufferedReader(new InputStreamReader(openFileInput("setting.txt")));
            if(reader != null) {
                String str = reader.readLine();
                String setting_list[] = str.split(",");
                if (setting_list[1] != null) {
                    int id = Integer.parseInt(setting_list[1]);
                    if (id == 0) {
                        deskImage.setImageResource(R.drawable.blue_modify);
                        BACKGROUND_MODE = 0;
                    } else if (id == 1) {
                        deskImage.setImageResource(R.drawable.bright_tree_modify);
                        BACKGROUND_MODE = 1;
                    } else if (id == 2) {
                        deskImage.setImageResource(R.drawable.green_modify);
                        BACKGROUND_MODE = 2;
                    } else if (id == 3) {
                        deskImage.setImageResource(R.drawable.red_modify);
                        BACKGROUND_MODE = 3;
                    } else if (id == 4) {
                        deskImage.setImageResource(R.drawable.tree_modify);
                        BACKGROUND_MODE = 4;
                    }
                }
            }
            else
                Toast.makeText(this,"배경설정을 불러오는데 에러가 발생했습니다.",Toast.LENGTH_LONG).show();
        }catch (Throwable t) {}
    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            init();
        }
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
                photo = null;

                //경로 가져와서 나타내주기
                scanning(bookCount, folderPath);
                Toast.makeText(getApplicationContext(),"추가하기 전 책의 인덱스 : " + bookCount+"",Toast.LENGTH_SHORT).show();
                bookCount++;
                Toast.makeText(getApplicationContext(),"추가할 책의 인덱스 : " + bookCount+"",Toast.LENGTH_SHORT).show();
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
                popup.setFocusable(false);
                break;

            // 리사이즈한 이미지 넘겨 받음
            case 1:
                if (resultCode != RESULT_OK)
                    return;

                final Bundle extras = data.getExtras();

                // 이미지를 저장하기 위한 파일 경로
                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PicKeep/" + bookName.getText().toString() + "/" +
                        bookName.getText().toString() + ".jpg";
                folderPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PicKeep/" + bookName.getText().toString() + "/";

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
                popup.setFocusable(false);
                break;

            case 30:
                String outName = data.getStringExtra("name");
                if (outName != null) {
                    if (outName.equals("blue_modify")) {
                        deskImage.setImageResource(R.drawable.blue_modify); BACKGROUND_MODE = 0;
                    } else if (outName.equals("bright_tree_modify")) {
                        deskImage.setImageResource(R.drawable.bright_tree_modify); BACKGROUND_MODE = 1;
                    } else if (outName.equals("green_modify")) {
                        deskImage.setImageResource(R.drawable.green_modify); BACKGROUND_MODE = 2;
                    } else if (outName.equals("red_modify")) {
                        deskImage.setImageResource(R.drawable.red_modify); BACKGROUND_MODE = 3;
                    } else if (outName.equals("tree_modify")) {
                        deskImage.setImageResource(R.drawable.tree_modify); BACKGROUND_MODE = 4;
                    }
                }
                break;
        }
    }

    //이미지를 사용자에 맞게 설정에 맞게 자르고, 그것을 해당 폴더에 저장한다.
    private void storeCropImage(Bitmap bitmap, String filePath) {
        File new_folder = new File(dirPath + "/" + bookName.getText().toString());
        if (!new_folder.exists()) {
            new_folder.mkdir();
        }

        File copyFile = new File(filePath); // 이거 위에 if문 체크랑 같이 연결하자

        //비트맵 형식으로 파일을 만든다.
        try {
            FileOutputStream out = new FileOutputStream(copyFile);
            photo.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        bookPhoto.setImageBitmap(bitmap);
    }

    public void showContents(View view) {
        int id = view.getId();
        int index = getIndex(id);
        Intent intent = new Intent(this, MainActivity.class);
        if(view.getVisibility() != View.INVISIBLE) { // visible
            intent.putExtra("path", bookPath[index] + "/");
            startActivity(intent);
        }
    }

    //메인에 메뉴를 추가해준다.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //메뉴를 선택할시에 작동할 이벤트
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int curId = item.getItemId();
        switch (curId) {
            case R.id.menu_edit:
                Intent intent = new Intent(this,EditsActivity.class);
                startActivityForResult(intent, 30);
                Log.i("vvvv","start!!");
                break;
            case R.id.menu_exit:
                finish(); //어플리케이션 끝내기
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setList() {
        File files = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/PicKeep/");
        File[] list = files.listFiles();
        int size = files.listFiles().length;
        if(size != 0) {
            Log.i("zxc", "size = " + size);
            for (int i = 0; i < size; i++) {
                String title = getTitleFromPath(list[i].getPath());
                Log.i("zxc", "title = " + title);
                Bitmap src = BitmapFactory.decodeFile(list[i].getPath() + "/" + title + ".jpg");
                Log.i("zxc", "image = " + list[i].getPath()  + "/" + title + ".jpg");
                bookArray[i + 1] = src;
                scanning(i + 1, list[i].getPath());
            }
            Toast.makeText(getApplicationContext(), "다시 로딩했을 경우 : " + bookCount + "", Toast.LENGTH_SHORT).show();
            bookCount = size + 1;
            Toast.makeText(getApplicationContext(), "다시 로딩후  : " + bookCount + "", Toast.LENGTH_SHORT).show();
        }
    }

    private String getTitleFromPath(String path) {
        String[] list = path.split("/");  // storage/emulated/0/KakaoTalkDownload/ -> storage[0] , emulated[1] , 0[2] , KakaoTalDownload[3]
        return list[list.length - 1];
    }

    //
    private void scanning(int bookCount, String folderPath) {
        Log.i("delete", "scanning 들어옴");
        if (bookCount == 1) {
            if (bookArray[1] != null)
                one.setImageBitmap(bookArray[1]);
            one.setVisibility(View.VISIBLE);
            bookPath[1] = folderPath;
        }

        if (bookCount == 2) {
            if (bookArray[2] != null)
                two.setImageBitmap(bookArray[2]);
            two.setVisibility(View.VISIBLE);
            bookPath[2] = folderPath;
        }
        if (bookCount == 3) {
            if (bookArray[3] != null)
                three.setImageBitmap(bookArray[3]);
            three.setVisibility(View.VISIBLE);
            bookPath[3] = folderPath;
        }
        if (bookCount == 4) {
            if (bookArray[5] != null)
                four.setImageBitmap(bookArray[4]);
            four.setVisibility(View.VISIBLE);
            bookPath[4] = folderPath;
        }
        if (bookCount == 5) {
            if (bookArray[5] != null)
                five.setImageBitmap(bookArray[5]);
            five.setVisibility(View.VISIBLE);
            bookPath[5] = folderPath;
        }
        if (bookCount == 6) {
            if (bookArray[6] != null)
                six.setImageBitmap(bookArray[6]);
            six.setVisibility(View.VISIBLE);
            bookPath[6] = folderPath;
        }
        if (bookCount == 7) {
            if (bookArray[7] != null)
                seven.setImageBitmap(bookArray[7]);
            seven.setVisibility(View.VISIBLE);
            bookPath[7] = folderPath;
        }
        if (bookCount == 8) {
            if (bookArray[8] != null)
                eight.setImageBitmap(bookArray[8]);
            eight.setVisibility(View.VISIBLE);
            bookPath[8] = folderPath;
        }
        if (bookCount == 9) {
            if (bookArray[9] != null)
                nine.setImageBitmap(bookArray[9]);
            nine.setVisibility(View.VISIBLE);
            bookPath[9] = folderPath;
        }
        if (bookCount == 10) {
            if (bookArray[10] != null)
                ten.setImageBitmap(bookArray[10]);
            ten.setVisibility(View.VISIBLE);
            bookPath[10] = folderPath;
        }

        if (bookCount == 11) {
            if (bookArray[11] != null)
                eleven.setImageBitmap(bookArray[11]);
            eleven.setVisibility(View.VISIBLE);
            bookPath[11] = folderPath;
        }
        if (bookCount == 12) {
            if (bookArray[12] != null)
                twelve.setImageBitmap(bookArray[12]);
            twelve.setVisibility(View.VISIBLE);
            bookPath[12] = folderPath;
        }
        if (bookCount == 13) {
            if (bookArray[13] != null)
                thirteen.setImageBitmap(bookArray[13]);
            thirteen.setVisibility(View.VISIBLE);
            bookPath[13] = folderPath;
        }
        if (bookCount == 14) {
            if (bookArray[14] != null)
                fourteen.setImageBitmap(bookArray[14]);
            fourteen.setVisibility(View.VISIBLE);
            bookPath[14] = folderPath;
        }

        Log.i("delete", "scanning 나감");
    }

    private void deleteFolder(String path) // 삭제하려는 폴더의 경로를 받아올 것.
    {
        File[] list;
        int size;
        File folder = new File(path);
        Log.i("zxc","folder path = "+folder.getPath());
        if(folder.exists() && folder.isDirectory()) // 폴더이고 존재하는 경우
        {
            list = folder.listFiles();
            size = list.length;

            for(int i=0; i<size; i++)   // 폴더 내부의 모든 파일(문서,폴더사진)을 삭제한다.
            {
                boolean result = list[i].delete();
                String doc_path = list[i].getPath();
                Log.i("zxc","delete result = "+result);
                getContentResolver().delete(MediaStore.Files.getContentUri("external"),
                        MediaStore.Files.FileColumns.DATA+" like ? ",
                        new String[]{doc_path});
                Log.i("zxc","file still exist? = "+list[i].exists());
            }
            String folder_path = folder.getPath();
            folder.delete();
            Log.i("zxc","folder still exist? = "+folder.exists());
            getContentResolver().delete(MediaStore.Files.getContentUri("external"),
                    MediaStore.Files.FileColumns.DATA+" like ? ",
                    new String[]{folder_path});
            Toast.makeText(this,"폴더를 삭제하였습니다.",Toast.LENGTH_LONG).show();
            bookCount--;
            Toast.makeText(getApplicationContext(),"책의 수 : " + bookCount+"",Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(this,"Error : Please contact with developer",Toast.LENGTH_LONG).show();
    }

    public void popup_delete(View v)
    {
        index = getIndex(v.getId());
        deleteObject = v;
        ViewGroup viewGroup = (RelativeLayout)findViewById(R.id.popup_delete);
        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        delete_window = new PopupWindow(this);
        View layout = inflater.inflate(R.layout.popup_delete,viewGroup);

        delete_window.setContentView(layout);
        delete_window.setWidth(500);
        delete_window.setHeight(500);
        delete_window.setFocusable(true);
        delete_window.showAtLocation(layout,Gravity.CENTER,30,30);

        Button ok_btn = (Button)layout.findViewById(R.id.ok);
        Button cancel_btn = (Button)layout.findViewById(R.id.cancel);

        ok_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                deleteFolder(bookPath[index]);
                Toast.makeText(getApplicationContext(),"index : "+index+"",Toast.LENGTH_SHORT).show();
                bookArray[index] = null;
                bookPath[index] = null;
                deleteObject.setVisibility(View.INVISIBLE);
                re_arrange(index);
                delete_window.dismiss();
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                delete_window.dismiss();
            }
        });
    }

    private int getIndex(int id)
    {
        switch(id)
        {
            case R.id.one : return 1;
            case R.id.two : return 2;
            case R.id.three : return 3;
            case R.id.four : return 4;
            case R.id.five : return 5;
            case R.id.six : return 6;
            case R.id.seven : return 7;
            case R.id.eight : return 8;
            case R.id.nine : return 9;
            case R.id.ten : return 10;
            case R.id.eleven : return 11;
            case R.id.twelve : return 12;
            case R.id.thirteen : return 13;
            case R.id.fourteen : return 14;
        }
        return 0;
    }

    private void registerLongClick()
    {
        for(int i=1; i<15; i++)
            button_list[i].setOnLongClickListener(listener);
    }

    private void re_arrange(int from)
    {
        Log.i("delete","index "+from+"부터 re_arrange를 시작");
        for(int front = from; front<= 13; front++) // front는 14까지가 한계
        {
            if(bookPath[front] == null)
            {
                Log.i("delete","index "+front+"가 null임 뒤에를 찾아보자");
                for (int back = front + 1; back <= 14; back++)
                {
                    if (bookPath[back] != null)
                    {
                        Log.i("delete","index "+back+"을 뒤에서 발견함.");
                        bookArray[front] = bookArray[back];
                        scanning(front,bookPath[back]);
                        bookArray[back] = null;
                        bookPath[back] = null;
                        button_list[back].setVisibility(View.INVISIBLE);
                        break;
                    }
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try{
            OutputStreamWriter out = new OutputStreamWriter(openFileOutput("setting.txt",MODE_PRIVATE));
            out.write("Background,"+BACKGROUND_MODE);
            out.close();
        }catch(Throwable t) {}
    }
}