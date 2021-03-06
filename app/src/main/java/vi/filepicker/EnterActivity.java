package vi.filepicker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import permissions.dispatcher.PermissionUtils;

public class EnterActivity extends AppCompatActivity {

    private EditText bookName;
    private ImageView bookPhoto;
    private Button folderAdd;
    private RelativeLayout viewGroup;
    private View create_layout;
    private View select_layout;
    private View delete_layout;
    private PopupWindow create_window;
    private PopupWindow select_window;
    private PopupWindow delete_window;
    private Bitmap gallery_photo;
    private Bitmap default_photo;
    private int past_index = 1;
    private int bookCount = 1;
    private int select_status = 0;
    private String[] bookPath;
    private String dirPath;
    private String folderPath;
    private String temp_text=""; //사용자가 도중에 작성한 책 제목을 다시 가져온다

    //책 이미지 버튼
    private ImageButton button_list[] = new ImageButton[16];

    private int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    ImageView deskImage;

    private int index;
    Button ok_btn; // delete_window
    Button cancel_btn; // delete_window
    Button ok; //create_window
    Button select_pic; // create_window
    Button select_book; // create_window
    Button cancel; //create_window
    Button select_ok;
    Button select_cancel;
    private int rb_index = 1;
    File directory_pickeep;

    // 책 선택 팝업창
    private ImageView get_select_iv[] = new ImageView[9];
    private LinearLayout get_select_ll[] = new LinearLayout[9];
    private RadioButton get_select_rb[] = new RadioButton[9];

    private View.OnLongClickListener listener;

    private View deleteObject;
    private int BACKGROUND_MODE = 4;

    private TextView text_list[];
    private BitmapFactory.Options options;

    private ClickListener mClickListener = new ClickListener();

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_main);

        Intent intent = new Intent(this, LoadingActivity.class);
        startActivity(intent);

        boolean readExternalPermission = PermissionUtils.hasSelfPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (readExternalPermission) { // 권한 체크 이상없으면
            init();
        } else { // 권한 체크 안되있으면
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            // 권한 요청 창 띄우기
        }
    }

    private void init() {
        dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PicKeep/";

        directory_pickeep = new File(dirPath);
        //폴더가 존재하지 않는다면 새로운 디렉토리 폴더를 생성한다.
        if (!directory_pickeep.exists()) {
            directory_pickeep.mkdir();
        }

        //책장배경
        deskImage = (ImageView) findViewById(R.id.deskImage);

        //책의 폴더 경로를 넣어주기 위해서
        bookPath = new String[16];
        text_list = new TextView[16];

        //이미지 경로와 폴더 경로 초기화
        for (int i = 0; i < 15; i++) {
            bookPath[i] = null;
            text_list[i] = null;
        }

        Log.d("EnterActivity", "Init start");
        options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inSampleSize = 2;
        setObjectList();
        setBackground(); // 처음에 로드될 때, 사용자가 설정한 기본배경으로 보여지도록 하는 설정을 담당.
        setList(); // 처음에 로드될 때, 폴더 리스트를 읽어들어와서 보여주도록 배치해주는 초기설정을 담당.
        registerLongClick(); // Long click 이벤트를 각 버튼에 추가하는 초기설정을 ㄷ마당.
        set_popup_create();
        setSelectBookCreate(); // 책 이미지 설정 팝업창
        set_popup_delete();  // Long click 해서 삭제하려고 할 때 뜨는 팝업의 초기설정을 담당.
        Log.d("EnterActivity", "Init end. all initialization process success!");
    }

    private void setObjectList() {
        button_list[1] = (ImageButton) findViewById(R.id.one);
        text_list[1] = (TextView) findViewById(R.id.textOne);
        button_list[2] = (ImageButton) findViewById(R.id.two);
        text_list[2] = (TextView) findViewById(R.id.textTwo);
        button_list[3] = (ImageButton) findViewById(R.id.three);
        text_list[3] = (TextView) findViewById(R.id.textThree);
        button_list[4] = (ImageButton) findViewById(R.id.four);
        text_list[4] = (TextView) findViewById(R.id.textFour);
        button_list[5] = (ImageButton) findViewById(R.id.five);
        text_list[5] = (TextView) findViewById(R.id.textFive);
        button_list[6] = (ImageButton) findViewById(R.id.six);
        text_list[6] = (TextView) findViewById(R.id.textSix);
        button_list[7] = (ImageButton) findViewById(R.id.seven);
        text_list[7] = (TextView) findViewById(R.id.textSeven);
        button_list[8] = (ImageButton) findViewById(R.id.eight);
        text_list[8] = (TextView) findViewById(R.id.textEight);
        button_list[9] = (ImageButton) findViewById(R.id.nine);
        text_list[9] = (TextView) findViewById(R.id.textNine);
        button_list[10] = (ImageButton) findViewById(R.id.ten);
        text_list[10] = (TextView) findViewById(R.id.textTen);
        button_list[11] = (ImageButton) findViewById(R.id.eleven);
        text_list[11] = (TextView) findViewById(R.id.textEleven);
        button_list[12] = (ImageButton) findViewById(R.id.twelve);
        text_list[12] = (TextView) findViewById(R.id.textTwelve);
        button_list[13] = (ImageButton) findViewById(R.id.thirteen);
        text_list[13] = (TextView) findViewById(R.id.textThirteen);
        button_list[14] = (ImageButton) findViewById(R.id.fourteen);
        text_list[14] = (TextView) findViewById(R.id.textFourteen);
        Log.d("EnterActivity", "Object initialization success");
    }

    private void setBackground() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(openFileInput("setting.txt")));
            if (reader != null) {
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
                Log.d("EnterActivity", "Loading background setting info success");
            } else
                Toast.makeText(this, "배경설정을 불러오는데 에러가 발생했습니다.", Toast.LENGTH_LONG).show();
            reader.close();
        } catch (Throwable t) {
        }
    }

    private void set_popup_create() {
        if (select_status == 1) {
            select_window.dismiss();
            select_status = 0;
        }
        default_photo = BitmapFactory.decodeResource(getResources(), R.drawable.book, options);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        create_layout = inflater.inflate(R.layout.popup_activity, (ViewGroup) findViewById(R.id.popup));
        create_window = new PopupWindow(this);
        create_window.setContentView(create_layout);
        create_window.setWidth(800);
        create_window.setHeight(1320);

        bookPhoto = (ImageView) create_layout.findViewById(R.id.getPic);
        // 책 이미지 가져오기
        setBookImage();
        //책이름(EditText)
        bookName = (EditText) create_layout.findViewById(R.id.getTitle);
        bookName.setText(temp_text);

        //사진 선택 버튼
        select_pic = (Button) create_layout.findViewById(R.id.select_pic);

        //책 선택 버튼
        select_book = (Button) create_layout.findViewById(R.id.select_book);

        // 사진 선택 버튼 클릭 시 internal 갤러리 띄우기
        select_pic.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, 0);
                create_window.setFocusable(false);
            }
        });

        // 책 이미지 선택 버튼 클릭 시 popup창 띄우기
        select_book.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                temp_text = bookName.getText().toString();
                past_index = rb_index;
                get_select_rb[rb_index].setChecked(true);
                showPopup(EnterActivity.this, 2);
            }
        });

        //확인 버튼을 누를 시 (수정해야함)
        ok = (Button) create_layout.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bookCount <= 14) {
                    if (bookName.getText().toString().length() == 0) // 책 제목을 넣지 않았다면,
                        Toast.makeText(getApplicationContext(), "책 제목을 입력하세요.", Toast.LENGTH_LONG).show();
                    else if (isContain(bookName.getText().toString()))
                        Toast.makeText(getApplicationContext(), "중복된 책이 존재합니다.", Toast.LENGTH_LONG).show();
                    else if (bookName.getText().toString().length() != 0) {
                        File new_folder = new File(dirPath + "/" + bookName.getText().toString());
                        if (!new_folder.exists()) {
                            new_folder.mkdir();
                            String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PicKeep/"
                                    + bookName.getText().toString() + "/" +
                                    bookName.getText().toString() + ".png";
                            if (gallery_photo != null)
                                storeCropImage(gallery_photo, filePath);
                            else {
                                getDefaultBitmap();
                                storeCropImage(default_photo, filePath);
                            }
                            rb_index=1;
                            getDefaultBitmap();
                            bookPhoto.setImageBitmap(default_photo);
                            gallery_photo = null;
                            create_window.dismiss();
                            folderPath = new_folder.getPath();
                            scanning(bookCount, folderPath);
                            bookCount++;
                            bookName.setText("");
                            Log.d("EnterActivity", "Folder creation success!");
                        }
                    }
                } else if (bookCount > 14) {
                    Toast.makeText(getApplicationContext(), "책장이 가득 찼습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 검색 버튼 누를 시 (이미지 검색)
        Button search;
        search = (Button) create_layout.findViewById(R.id.search);

        search.setOnClickListener(new View.OnClickListener() {

            public void onClick(View V) {
                if (bookName.getText().toString().length() == 0) // 책 제목을 넣지 않았다면,
                    Toast.makeText(getApplicationContext(), "책 제목을 입력한 후 검색하세요.", Toast.LENGTH_LONG).show();
                else {
                    String getUri = "https://www.google.com/search?tbm=isch&q=" + bookName.getText();
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getUri));
                    startActivity(intent);
                }
            }
        });

        //취소 버튼을 누를시
        cancel = (Button) create_layout.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                rb_index = 1;
                setBookImage();
                bookName.setText("");
                create_window.dismiss();
            }
        });

        folderAdd = (Button) findViewById(R.id.imageButton);

        //메인화면에서 +버튼을 누를 경우 실행됨
        folderAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showPopup(EnterActivity.this, 1);
            }
        });
    }

    // '+'버튼 누를 시 팝업창이 띄어진다.
    public void showPopup(final Activity context, int mode) {
        //팝업창의 위치
        int OFFSET_X = 0;
        int OFFSET_Y = 0;

        // '+'버튼 누를 시 팝업창 뜨게함
        if(mode==1) {
            if (create_window != null) {
                if (select_window != null) {
                    select_window.dismiss();
                }
                create_window.setFocusable(true);
                create_window.showAtLocation(create_layout, Gravity.CENTER, OFFSET_X, OFFSET_Y);
            }
        }

        // 책 이미지 선택 누를 시 팝업창 뜨게함
        else if(mode==2){
            select_window.setFocusable(true);
            select_window.showAtLocation(select_layout, Gravity.CENTER, OFFSET_X, OFFSET_Y);
        }
    }

    // 책 이미지 팝업창 띄우기
    private void setSelectBookCreate() {
        LayoutInflater infalter = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        select_layout = infalter.inflate(R.layout.popup_select_book, (ViewGroup) findViewById(R.id.select_book_popup));
        select_window = new PopupWindow(this);
        select_window.setContentView(select_layout);
        select_window.setWidth(800);
        select_window.setHeight(1300);

        setSelectId(select_layout);

        // 사용자가 이미지 눌렀을 시 라디오버튼 check되게 한다
        get_select_iv[1].setOnClickListener(mClickListener);
        get_select_ll[1].setOnClickListener(mClickListener);
        get_select_iv[2].setOnClickListener(mClickListener);
        get_select_ll[2].setOnClickListener(mClickListener);
        get_select_iv[3].setOnClickListener(mClickListener);
        get_select_ll[3].setOnClickListener(mClickListener);
        get_select_iv[4].setOnClickListener(mClickListener);
        get_select_ll[4].setOnClickListener(mClickListener);
        get_select_iv[5].setOnClickListener(mClickListener);
        get_select_ll[5].setOnClickListener(mClickListener);
        get_select_iv[6].setOnClickListener(mClickListener);
        get_select_ll[6].setOnClickListener(mClickListener);
        get_select_iv[7].setOnClickListener(mClickListener);
        get_select_ll[7].setOnClickListener(mClickListener);
        get_select_iv[8].setOnClickListener(mClickListener);
        get_select_ll[8].setOnClickListener(mClickListener);


        select_ok.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                setBookImage();
                offTheRadio();
                showPopup(EnterActivity.this, 1);
            }
        });

        select_cancel.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                rb_index = past_index;
                offTheRadio();
                showPopup(EnterActivity.this, 1);
            }

        });

    }

    // 책 이미지 팝업창 아이디 부여
    private void setSelectId(View select_context) {

        get_select_iv[1] = (ImageView) select_context.findViewById(R.id.book_iv_1);
        get_select_iv[2] = (ImageView) select_context.findViewById(R.id.book_iv_2);
        get_select_iv[3] = (ImageView) select_context.findViewById(R.id.book_iv_3);
        get_select_iv[4] = (ImageView) select_context.findViewById(R.id.book_iv_4);
        get_select_iv[5] = (ImageView) select_context.findViewById(R.id.book_iv_5);
        get_select_iv[6] = (ImageView) select_context.findViewById(R.id.book_iv_6);
        get_select_iv[7] = (ImageView) select_context.findViewById(R.id.book_iv_7);
        get_select_iv[8] = (ImageView) select_context.findViewById(R.id.book_iv_8);

        get_select_ll[1] = (LinearLayout) select_context.findViewById(R.id.book_rb_ll_1);
        get_select_ll[2] = (LinearLayout) select_context.findViewById(R.id.book_rb_ll_2);
        get_select_ll[3] = (LinearLayout) select_context.findViewById(R.id.book_rb_ll_3);
        get_select_ll[4] = (LinearLayout) select_context.findViewById(R.id.book_rb_ll_4);
        get_select_ll[5] = (LinearLayout) select_context.findViewById(R.id.book_rb_ll_5);
        get_select_ll[6] = (LinearLayout) select_context.findViewById(R.id.book_rb_ll_6);
        get_select_ll[7] = (LinearLayout) select_context.findViewById(R.id.book_rb_ll_7);
        get_select_ll[8] = (LinearLayout) select_context.findViewById(R.id.book_rb_ll_8);

        get_select_rb[1] = (RadioButton) select_context.findViewById(R.id.book_rb_1);
        get_select_rb[2] = (RadioButton) select_context.findViewById(R.id.book_rb_2);
        get_select_rb[3] = (RadioButton) select_context.findViewById(R.id.book_rb_3);
        get_select_rb[4] = (RadioButton) select_context.findViewById(R.id.book_rb_4);
        get_select_rb[5] = (RadioButton) select_context.findViewById(R.id.book_rb_5);
        get_select_rb[6] = (RadioButton) select_context.findViewById(R.id.book_rb_6);
        get_select_rb[7] = (RadioButton) select_context.findViewById(R.id.book_rb_7);
        get_select_rb[8] = (RadioButton) select_context.findViewById(R.id.book_rb_8);

        select_ok = (Button) select_context.findViewById(R.id.select_ok);
        select_cancel = (Button) select_context.findViewById(R.id.select_cancel);
    }

    private class ClickListener implements View.OnClickListener {

        public void onClick(View v) {
            String getName = getResources().getResourceName(v.getId());
            int getNum = Integer.parseInt(getName.substring(getName.length() - 1, getName.length()));

            // 체크된 라디오버튼 삭제
            offTheRadio();

            get_select_rb[getNum].setChecked(true);
            rb_index = getNum;
        }
    }

    // 체크된 라디오버튼 삭제
    private void offTheRadio() {

        for(int i=1; i<=8; i++){
            get_select_rb[i].setChecked(false);
        }
    }

    // 팝업창 책 이미지 가져오기
    private void setBookImage() {
        switch (rb_index) {
            case 1:
                bookPhoto.setImageResource(R.drawable.book);
                break;
            case 2:
                bookPhoto.setImageResource(R.drawable.book2);
                break;
            case 3:
                bookPhoto.setImageResource(R.drawable.book3);
                break;
            case 4:
                bookPhoto.setImageResource(R.drawable.book4);
                break;
            case 5:
                bookPhoto.setImageResource(R.drawable.book5);
                break;
            case 6:
                bookPhoto.setImageResource(R.drawable.book6);
                break;
            case 7:
                bookPhoto.setImageResource(R.drawable.book7);
                break;
            case 8:
                bookPhoto.setImageResource(R.drawable.book8);
                break;
        }

    }

    private void getDefaultBitmap(){
        switch (rb_index){
            case 1:
                default_photo = BitmapFactory.decodeResource(getResources(), R.drawable.book, options);
                break;
            case 2:
                default_photo = BitmapFactory.decodeResource(getResources(), R.drawable.book2, options);
                break;
            case 3:
                default_photo = BitmapFactory.decodeResource(getResources(), R.drawable.book3, options);
                break;
            case 4:
                default_photo = BitmapFactory.decodeResource(getResources(), R.drawable.book4, options);
                break;
            case 5:
                default_photo = BitmapFactory.decodeResource(getResources(), R.drawable.book5, options);
                break;
            case 6:
                default_photo = BitmapFactory.decodeResource(getResources(), R.drawable.book6, options);
                break;
            case 7:
                default_photo = BitmapFactory.decodeResource(getResources(), R.drawable.book7, options);
                break;
            case 8:
                default_photo = BitmapFactory.decodeResource(getResources(), R.drawable.book8, options);
                break;
        }

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
                Log.d("EnterActivity", "Image load from gallery success");
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(getUri, "image/*");

                // 200*200 크기로 저장
                intent.putExtra("outputX", 200);
                intent.putExtra("outputY", 200);
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, 1);
                create_window.setFocusable(false);
                break;

            // 리사이즈한 이미지 넘겨 받음
            case 1:
                if (resultCode != RESULT_OK)
                    return;

                Log.d("EnterActivity", "Image crop success!");
                final Bundle extras = data.getExtras();

                // 이미지를 저장하기 위한 파일 경로
                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PicKeep/" + bookName.getText().toString() + "/" +
                        bookName.getText().toString() + ".png";
                folderPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PicKeep/" + bookName.getText().toString() + "/";

                if (extras != null) {

                    gallery_photo = extras.getParcelable("data");

                    bookPhoto.setImageBitmap(gallery_photo); // 이미지 뷰에 그림 삽입하기
                    //storeCropImage(gallery_photo, filePath);  // 어차피 OK버튼 누를 때 이미지 저장함 중복적용 되어서 삭제예정.
                    break;
                }

                //원래 경로의 똑같은 파일 이름이 있으시 기존의 파일을 삭제한다.
                File f = new File(getUri.getPath());
                if (f.exists()) {
                    f.delete();
                }
                create_window.setFocusable(false);
                break;

            case 30:
                String outName = data.getStringExtra("name");
                if (outName != null) {
                    if (outName.equals("blue_modify")) {
                        deskImage.setImageResource(R.drawable.blue_modify);
                        BACKGROUND_MODE = 0;
                    } else if (outName.equals("bright_tree_modify")) {
                        deskImage.setImageResource(R.drawable.bright_tree_modify);
                        BACKGROUND_MODE = 1;
                    } else if (outName.equals("green_modify")) {
                        deskImage.setImageResource(R.drawable.green_modify);
                        BACKGROUND_MODE = 2;
                    } else if (outName.equals("red_modify")) {
                        deskImage.setImageResource(R.drawable.red_modify);
                        BACKGROUND_MODE = 3;
                    } else if (outName.equals("tree_modify")) {
                        deskImage.setImageResource(R.drawable.tree_modify);
                        BACKGROUND_MODE = 4;
                    }
                }
                break;
        }
    }

    //이미지를 사용자에 맞게 설정에 맞게 자르고, 그것을 해당 폴더에 저장한다.
    private void storeCropImage(Bitmap bitmap, String filePath) {
        File copyFile = new File(filePath); // 이거 위에 if문 체크랑 같이 연결하자
        //비트맵 형식으로 파일을 만든다.
        try {
            FileOutputStream out = new FileOutputStream(copyFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
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
        if (bookPath[index] != null) {
            intent.putExtra("path", bookPath[index] + "/");
        }
        startActivity(intent);
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
                Intent intent = new Intent(this, EditsActivity.class);
                startActivityForResult(intent, 30);
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
        if (size != 0 && size < 15) {
            Log.i("zxc", "size = " + size);
            for (int i = 0; i < size; i++) {
                String title = getTitleFromPath(list[i].getPath());
                scanning(i + 1, list[i].getPath());
            }
            bookCount = size + 1;
            Log.d("EnterActivity", "Loading folder list success. current folder count = " + bookCount);
        }
    }

    public String getTitleFromPath(String path) {
        String[] list = path.split("/");  // storage/emulated/0/KakaoTalkDownload/ -> storage[0] , emulated[1] , 0[2] , KakaoTalDownload[3]
        return list[list.length - 1];
    }

    //
    private void scanning(int bookCount, String folderPath) {
        if (folderPath != null) {
            button_list[bookCount].setVisibility(View.VISIBLE);
            bookPath[bookCount] = folderPath;
            setImageOnButton(bookCount);
            text_list[bookCount].setText(getTitleFromPath(folderPath));  // 폴더 이름 정해주기
            text_list[bookCount].setVisibility(View.VISIBLE); // 폴더 이름 보여주기
        }
    }

    private void setImageOnButton(int index) {
        String img_path = bookPath[index] + "/" + getTitleFromPath(bookPath[index]) + ".png";
        try {
            File img_file = new File(img_path);
            if (img_file.exists()) {
                Bitmap src = BitmapFactory.decodeFile(img_path, options);
                button_list[index].setImageBitmap(src);
            }
        } catch (Throwable t) {
        }
    }

    private void deleteFolder(String path) // 삭제하려는 폴더의 경로를 받아올 것.
    {
        File[] list;
        int size;
        File folder = new File(path);
        if (folder.exists() && folder.isDirectory()) // 폴더이고 존재하는 경우
        {
            Log.d("EnterActivity", "Folder Delete start");
            list = folder.listFiles();
            size = list.length;

            for (int i = 0; i < size; i++)   // 폴더 내부의 모든 파일(문서,폴더사진)을 삭제한다.
            {
                boolean result = list[i].delete();
                String doc_path = list[i].getPath();
                getContentResolver().delete(MediaStore.Files.getContentUri("external"),
                        MediaStore.Files.FileColumns.DATA + " like ? ",
                        new String[]{doc_path});
                Log.d("EnterActivity", "Deleting file in this folder. Left files = " + (size - i));
            }
            Log.d("EnterActivity", "All files deletion sucess!");
            String folder_path = folder.getPath();
            folder.delete();
            Log.d("EnterActivity", "Folder delete success!");
            getContentResolver().delete(MediaStore.Files.getContentUri("external"),
                    MediaStore.Files.FileColumns.DATA + " like ? ",
                    new String[]{folder_path});
            Log.d("EnterActivity", "Deletion result update on database success");
            bookCount--;
        } else
            Toast.makeText(this, "Error : Please contact with developer", Toast.LENGTH_LONG).show();
    }

    private void set_popup_delete()  // 팝업창이 뜰 때마다 layout을 불러들이는 비용을 줄였음.
    {
        if (delete_layout == null) {
            ViewGroup viewGroup = (RelativeLayout) findViewById(R.id.popup_delete);
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            delete_layout = inflater.inflate(R.layout.popup_delete, viewGroup);
            delete_window = new PopupWindow(this);
            delete_window.setContentView(delete_layout);
            delete_window.getHeight();
            delete_window.getWidth();
            delete_window.setFocusable(true);

            ok_btn = (Button) delete_layout.findViewById(R.id.ok);
            cancel_btn = (Button) delete_layout.findViewById(R.id.cancel);

            ok_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteFolder(bookPath[index]);
                    Toast.makeText(getApplicationContext(), "폴더가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    bookPath[index] = null;
                    deleteObject.setVisibility(View.INVISIBLE);
                    text_list[index].setText(""); // 폴더 삭제시 이름도 초기화
                    text_list[index].setVisibility(View.INVISIBLE);  // 폴더 삭제시 이름 부분도 안보이게
                    re_arrange(index);
                    delete_window.dismiss();
                }
            });

            cancel_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delete_window.dismiss();
                }
            });
        }
    }

    public void popup_delete(View v) {
        index = getIndex(v.getId());
        deleteObject = v;

        if (delete_layout == null) {
            Toast.makeText(this, "popup delete is null", Toast.LENGTH_LONG).show();
        } else {
            delete_window.showAtLocation(delete_layout, Gravity.CENTER, 0, 0);
            //delete_window.showAtLocation(delete_layout, Gravity.CENTER, 30, 30);
        }
    }

    private int getIndex(int id) {
        switch (id) {
            case R.id.one:
                return 1;
            case R.id.two:
                return 2;
            case R.id.three:
                return 3;
            case R.id.four:
                return 4;
            case R.id.five:
                return 5;
            case R.id.six:
                return 6;
            case R.id.seven:
                return 7;
            case R.id.eight:
                return 8;
            case R.id.nine:
                return 9;
            case R.id.ten:
                return 10;
            case R.id.eleven:
                return 11;
            case R.id.twelve:
                return 12;
            case R.id.thirteen:
                return 13;
            case R.id.fourteen:
                return 14;
        }
        return 0;
    }

    private void registerLongClick() {
        listener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                popup_delete(v);
                return true;
            }
        };
        for (int i = 1; i < 15; i++)
            button_list[i].setOnLongClickListener(listener);
    }

    private void re_arrange(int from) {
        Log.i("delete", "index " + from + "부터 re_arrange를 시작");
        for (int front = from; front <= 13; front++) // front는 14까지가 한계
        {
            if (bookPath[front] == null) {
                Log.i("delete", "index " + front + "가 null임 뒤에를 찾아보자");
                for (int back = front + 1; back <= 14; back++) {
                    if (bookPath[back] != null) {
                        Log.i("delete", "index " + back + "을 뒤에서 발견함.");
                        scanning(front, bookPath[back]);  // front 위치를 업데이트 한다.
                        bookPath[front] = bookPath[back];
                        bookPath[back] = null;
                        text_list[back].setText("");
                        text_list[back].setVisibility(View.INVISIBLE);
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
        try {
            OutputStreamWriter out = new OutputStreamWriter(openFileOutput("setting.txt", MODE_PRIVATE));
            out.write("Background," + BACKGROUND_MODE);
            out.close();
        } catch (Throwable t) {
        }
    }

    private boolean isContain(String title) {
        File files = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/PicKeep/");
        File[] list = files.listFiles();
        for (int i = 0; i < list.length; i++) {
            if (getTitleFromPath(list[i].getPath()).equals(title))
                return true;
        }
        return false;
    }

    public int getCount() {
        return bookCount;
    }
}